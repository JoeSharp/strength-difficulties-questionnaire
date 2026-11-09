package rest

import (
	"encoding/json"
	"errors"
	"io"
	"log"
	"net/http"
	"slices"
	"strings"
	"time"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	domain "joesharpcs.co.uk/sdq/domain"
)

type greetRequest struct {
	Name string `json:"name"`
}

type greetResponse struct {
	Name     string `json:"name"`
	Greeting string `json:"greeting"`
}

type createClientRequest struct {
	CodeName                  string                `json:"codeName"`
	DateOfBirth               string                `json:"dateOfBirth"`
	Gender                    *string               `json:"gender"`
	Council                   *string               `json:"council"`
	Ethnicity                 *string               `json:"ethnicity"`
	EnglishAdditionalLanguage *string               `json:"englishAdditionalLanguage"`
	DisabilityStatus          *string               `json:"disabilityStatus"`
	DisabilityTypes           []string              `json:"disabilityTypes"`
	CareExperience            *string               `json:"careExperience"`
	Interventions             []domain.Intervention `json:"interventions"`
	Aces                      map[string]int        `json:"aces"`
	FundingSource             *string               `json:"fundingSource"`
}

func CreateRestApi(clients domain.SdqClientRepository) *chi.Mux {
	r := chi.NewRouter()
	r.Use(middleware.Logger)

	// Mount API routes under /api
	r.Route("/api", func(api chi.Router) {
		api.Get("/health", func(w http.ResponseWriter, r *http.Request) {
			log.Println("Health check endpoint called")
			w.Write([]byte("ok"))
		})

		api.Post("/greet", func(w http.ResponseWriter, r *http.Request) {
			var req greetRequest
			if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
				log.Printf("failed to decode request body: %v", err)
				http.Error(w, "invalid JSON body", http.StatusBadRequest)
				return
			}
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(greetResponse{Name: req.Name, Greeting: "Hello " + req.Name})
		})

		api.Route("/clients", func(c chi.Router) {
			c.Get("/", func(w http.ResponseWriter, r *http.Request) {
				list, err := clients.GetClients()
				if err != nil {
					log.Printf("failed to get clients: %v", err)
					http.Error(w, "failed to retrieve clients", http.StatusInternalServerError)
					return
				}
				if list == nil {
					list = []domain.SdqClient{}
				}
				w.Header().Set("Content-Type", "application/json")
				json.NewEncoder(w).Encode(list)
			})

			c.Post("/", func(w http.ResponseWriter, r *http.Request) {
				var req createClientRequest
				decoder := json.NewDecoder(http.MaxBytesReader(w, r.Body, 1<<20))
				decoder.DisallowUnknownFields()
				if err := decoder.Decode(&req); err != nil {
					log.Printf("failed to decode client request: %v", err)
					http.Error(w, "invalid client data", http.StatusBadRequest)
					return
				}
				if err := decoder.Decode(&struct{}{}); !errors.Is(err, io.EOF) {
					log.Printf("client request contains trailing data: %v", err)
					http.Error(w, "invalid client data", http.StatusBadRequest)
					return
				}

				req.CodeName = strings.TrimSpace(req.CodeName)
				if req.CodeName == "" {
					http.Error(w, "codeName is required", http.StatusBadRequest)
					return
				}
				if _, err := time.Parse("2006-01-02", req.DateOfBirth); err != nil {
					http.Error(w, "dateOfBirth must be a valid date in YYYY-MM-DD format", http.StatusBadRequest)
					return
				}

				allowedValues := map[string][]string{
					"gender":                    {"MALE", "FEMALE", "NON_BINARY", "OTHER", "PREFER_NOT_TO_SAY"},
					"council":                   {"CHELTENHAM", "GLOUCESTER_CITY", "STROUD", "TEWKESBURY", "FOREST_OF_DEAN", "NORTH_COTSWOLDS", "OUT_OF_COUNTY", "UNKNOWN"},
					"ethnicity":                 {"WHITE_BRITISH", "WHITE_EUROPEAN", "MIXED", "ASIAN", "BLACK", "TRAVELLER", "OTHER"},
					"englishAdditionalLanguage": {"YES", "NO", "PREFER_NOT_TO_SAY"},
					"disabilityStatus":          {"DISABILITY", "NO_DISABILITY", "PREFER_NOT_TO_SAY"},
					"careExperience":            {"NO", "YES_ADOPTED", "YES_CHILD_IN_CARE", "SGO", "KINSHIP", "UNKNOWN"},
					"fundingSource":             {"EHCP", "PEP", "ASGSF", "PRIVATE", "OTHER_CHARITABLE", "SUBSIDISED_SESSION_FUND", "PROJECT", "UNKNOWN"},
				}
				for name, value := range map[string]*string{
					"gender":                    req.Gender,
					"council":                   req.Council,
					"ethnicity":                 req.Ethnicity,
					"englishAdditionalLanguage": req.EnglishAdditionalLanguage,
					"disabilityStatus":          req.DisabilityStatus,
					"careExperience":            req.CareExperience,
					"fundingSource":             req.FundingSource,
				} {
					normalized := optionalValue(value)
					if normalized != nil && !slices.Contains(allowedValues[name], *normalized) {
						http.Error(w, "invalid "+name, http.StatusBadRequest)
						return
					}
				}
				disabilityStatus := optionalValue(req.DisabilityStatus)

				allowedDisabilityTypes := []string{
					"PHYSICAL", "SENSORY", "LEARNING", "NEURODIVERSE",
					"MENTAL_HEALTH_CONDITION", "CHRONIC", "SPEECH_OR_COMMUNICATION",
					"COGNITIVE_OR_MEMORY", "OTHER", "NOT_APPLICABLE", "PREFER_NOT_TO_SAY",
				}
				disabilityTypesSeen := make(map[string]struct{}, len(req.DisabilityTypes))
				for _, value := range req.DisabilityTypes {
					if !slices.Contains(allowedDisabilityTypes, value) {
						http.Error(w, "invalid disability type", http.StatusBadRequest)
						return
					}
					if _, exists := disabilityTypesSeen[value]; exists {
						http.Error(w, "duplicate disability type", http.StatusBadRequest)
						return
					}
					disabilityTypesSeen[value] = struct{}{}
				}
				if len(req.DisabilityTypes) > 1 {
					if _, notApplicable := disabilityTypesSeen["NOT_APPLICABLE"]; notApplicable {
						http.Error(w, "NOT_APPLICABLE cannot be combined with another disability type", http.StatusBadRequest)
						return
					}
					if _, preferNotToSay := disabilityTypesSeen["PREFER_NOT_TO_SAY"]; preferNotToSay {
						http.Error(w, "PREFER_NOT_TO_SAY cannot be combined with another disability type", http.StatusBadRequest)
						return
					}
				}
				if disabilityStatus != nil && *disabilityStatus == "PREFER_NOT_TO_SAY" {
					if len(req.DisabilityTypes) == 0 {
						req.DisabilityTypes = []string{"PREFER_NOT_TO_SAY"}
						disabilityTypesSeen["PREFER_NOT_TO_SAY"] = struct{}{}
					} else if len(req.DisabilityTypes) != 1 || req.DisabilityTypes[0] != "PREFER_NOT_TO_SAY" {
						http.Error(w, "PREFER_NOT_TO_SAY disability status requires the matching disability type", http.StatusBadRequest)
						return
					}
				} else if _, preferNotToSay := disabilityTypesSeen["PREFER_NOT_TO_SAY"]; preferNotToSay {
					http.Error(w, "PREFER_NOT_TO_SAY disability type requires the matching disability status", http.StatusBadRequest)
					return
				}
				if disabilityStatus != nil && *disabilityStatus == "NO_DISABILITY" {
					if len(req.DisabilityTypes) == 0 {
						req.DisabilityTypes = []string{"NOT_APPLICABLE"}
					} else if len(req.DisabilityTypes) != 1 || req.DisabilityTypes[0] != "NOT_APPLICABLE" {
						http.Error(w, "NO_DISABILITY status requires the NOT_APPLICABLE disability type", http.StatusBadRequest)
						return
					}
				} else if _, notApplicable := disabilityTypesSeen["NOT_APPLICABLE"]; notApplicable {
					http.Error(w, "NOT_APPLICABLE disability type requires the NO_DISABILITY status", http.StatusBadRequest)
					return
				}

				allowedInterventions := []string{"CCPT", "CPRT", "PTP", "IA", "UNKNOWN"}
				for _, intervention := range req.Interventions {
					if !slices.Contains(allowedInterventions, intervention.Type) || intervention.Sessions < 0 {
						http.Error(w, "invalid intervention", http.StatusBadRequest)
						return
					}
				}

				allowedAces := []string{
					"COMMUNITY", "SOCIO_ECONOMIC", "DISCRIMINATION", "HEALTH", "EDUCATION",
					"BEREAVEMENT", "DIGITAL_ONLINE", "ENVIRONMENTAL", "CHILD_WELFARE", "GENERIC",
				}
				for aceType, score := range req.Aces {
					if !slices.Contains(allowedAces, aceType) || score < 0 {
						http.Error(w, "invalid ACE value", http.StatusBadRequest)
						return
					}
				}

				client, err := clients.CreateClient(domain.SdqClient{
					CodeName:                  req.CodeName,
					DateOfBirth:               req.DateOfBirth,
					Gender:                    optionalValue(req.Gender),
					Council:                   optionalValue(req.Council),
					Ethnicity:                 optionalValue(req.Ethnicity),
					EnglishAdditionalLanguage: optionalValue(req.EnglishAdditionalLanguage),
					DisabilityStatus:          disabilityStatus,
					DisabilityTypes:           req.DisabilityTypes,
					CareExperience:            optionalValue(req.CareExperience),
					Interventions:             req.Interventions,
					Aces:                      req.Aces,
					FundingSource:             optionalValue(req.FundingSource),
				})
				if err != nil {
					log.Printf("failed to create client: %v", err)
					http.Error(w, "failed to create client", http.StatusInternalServerError)
					return
				}

				w.Header().Set("Content-Type", "application/json")
				w.WriteHeader(http.StatusCreated)
				if err := json.NewEncoder(w).Encode(client); err != nil {
					log.Printf("failed to encode created client: %v", err)
				}
			})
		})
	})

	return r
}

func optionalValue(value *string) *string {
	if value == nil {
		return nil
	}
	trimmed := strings.TrimSpace(*value)
	if trimmed == "" {
		return nil
	}
	return &trimmed
}
