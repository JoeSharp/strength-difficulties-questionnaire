package rest

import (
	"encoding/json"
	"errors"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"github.com/google/uuid"
	domain "joesharpcs.co.uk/sdq/domain"
)

type fakeRepo struct {
	clients []domain.SdqClient
	created domain.SdqClient
	err     error
}

func (f fakeRepo) GetClients() ([]domain.SdqClient, error) { return f.clients, f.err }
func (f fakeRepo) CreateClient(client domain.SdqClient) (domain.SdqClient, error) {
	if f.err != nil {
		return domain.SdqClient{}, f.err
	}
	if f.created.ID != uuid.Nil {
		return f.created, nil
	}
	return client, nil
}

func get(repo domain.SdqClientRepository) *httptest.ResponseRecorder {
	rec := httptest.NewRecorder()
	CreateRestApi(repo).ServeHTTP(rec, httptest.NewRequest("GET", "/api/clients", nil))
	return rec
}

func TestGetClients(t *testing.T) {
	id := uuid.New()
	rec := get(fakeRepo{clients: []domain.SdqClient{{ID: id, CodeName: "abc"}}})
	if rec.Code != http.StatusOK {
		t.Fatalf("status %d", rec.Code)
	}
	want := `[{"id":"` + id.String() + `","codeName":"abc"}]`
	if strings.TrimSpace(rec.Body.String()) != want {
		t.Fatalf("got %s", rec.Body.String())
	}
}

func TestGetClientsEmptyIsArray(t *testing.T) {
	if body := strings.TrimSpace(get(fakeRepo{}).Body.String()); body != "[]" {
		t.Fatalf("got %s", body)
	}
}

func TestGetClientsError(t *testing.T) {
	if code := get(fakeRepo{err: errors.New("boom")}).Code; code != http.StatusInternalServerError {
		t.Fatalf("status %d", code)
	}
}

func TestCreateClient(t *testing.T) {
	id := uuid.New()
	gender := "FEMALE"
	created := domain.SdqClient{
		ID:              id,
		CodeName:        "new client",
		DateOfBirth:     "2015-04-03",
		SubmittedAt:     timePtr(time.Date(2026, 11, 5, 20, 0, 0, 0, time.UTC)),
		Gender:          &gender,
		DisabilityTypes: []string{"SENSORY"},
		Interventions:   []domain.Intervention{{Type: "CPRT", Sessions: 4}},
		Aces:            map[string]int{"GENERIC": 3},
	}
	body := `{"codeName":" new client ","dateOfBirth":"2015-04-03","gender":"FEMALE","disabilityTypes":["SENSORY"],"interventions":[{"type":"CPRT","sessions":4}],"aces":{"GENERIC":3}}`
	rec := httptest.NewRecorder()
	CreateRestApi(fakeRepo{created: created}).ServeHTTP(
		rec,
		httptest.NewRequest(http.MethodPost, "/api/clients", strings.NewReader(body)),
	)
	if rec.Code != http.StatusCreated {
		t.Fatalf("status %d: %s", rec.Code, rec.Body.String())
	}

	var response domain.SdqClient
	if err := json.Unmarshal(rec.Body.Bytes(), &response); err != nil {
		t.Fatalf("decode response: %v", err)
	}
	if response.ID != id || response.CodeName != "new client" || len(response.DisabilityTypes) != 1 ||
		len(response.Interventions) != 1 || response.Aces["GENERIC"] != 3 ||
		response.SubmittedAt == nil || !response.SubmittedAt.Equal(*created.SubmittedAt) {
		t.Fatalf("unexpected response: %+v", response)
	}
}

func timePtr(value time.Time) *time.Time {
	return &value
}

func TestCreateClientAutoFillsPreferNotToSayDisabilityType(t *testing.T) {
	body := `{"codeName":"client","dateOfBirth":"2015-04-03","disabilityStatus":"PREFER_NOT_TO_SAY"}`
	rec := httptest.NewRecorder()
	CreateRestApi(fakeRepo{}).ServeHTTP(
		rec,
		httptest.NewRequest(http.MethodPost, "/api/clients", strings.NewReader(body)),
	)
	if rec.Code != http.StatusCreated {
		t.Fatalf("status %d: %s", rec.Code, rec.Body.String())
	}

	var response domain.SdqClient
	if err := json.Unmarshal(rec.Body.Bytes(), &response); err != nil {
		t.Fatalf("decode response: %v", err)
	}
	if len(response.DisabilityTypes) != 1 || response.DisabilityTypes[0] != "PREFER_NOT_TO_SAY" {
		t.Fatalf("disability types = %v", response.DisabilityTypes)
	}
}

func TestCreateClientAutoFillsNotApplicableDisabilityType(t *testing.T) {
	body := `{"codeName":"client","dateOfBirth":"2015-04-03","disabilityStatus":"NO_DISABILITY"}`
	rec := httptest.NewRecorder()
	CreateRestApi(fakeRepo{}).ServeHTTP(
		rec,
		httptest.NewRequest(http.MethodPost, "/api/clients", strings.NewReader(body)),
	)
	if rec.Code != http.StatusCreated {
		t.Fatalf("status %d: %s", rec.Code, rec.Body.String())
	}

	var response domain.SdqClient
	if err := json.Unmarshal(rec.Body.Bytes(), &response); err != nil {
		t.Fatalf("decode response: %v", err)
	}
	if len(response.DisabilityTypes) != 1 || response.DisabilityTypes[0] != "NOT_APPLICABLE" {
		t.Fatalf("disability types = %v", response.DisabilityTypes)
	}
}

func TestCreateClientRejectsInvalidInput(t *testing.T) {
	for _, body := range []string{
		`{"codeName":"","dateOfBirth":"2015-04-03"}`,
		`{"codeName":"client","dateOfBirth":"invalid"}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","gender":"unknown"}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","interventions":[{"type":"bad","sessions":1}]}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","aces":{"UNKNOWN":2}}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","disabilityTypes":["PREFER_NOT_TO_SAY","SENSORY"]}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","disabilityStatus":"NO_DISABILITY","disabilityTypes":["SENSORY"]}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","disabilityStatus":"DISABILITY","disabilityTypes":["NOT_APPLICABLE"]}`,
		`{"codeName":"client","dateOfBirth":"2015-04-03","submittedAt":"2026-11-05T20:00:00Z"}`,
	} {
		rec := httptest.NewRecorder()
		CreateRestApi(fakeRepo{}).ServeHTTP(
			rec,
			httptest.NewRequest(http.MethodPost, "/api/clients", strings.NewReader(body)),
		)
		if rec.Code != http.StatusBadRequest {
			t.Errorf("body %s: status %d, want %d", body, rec.Code, http.StatusBadRequest)
		}
	}
}
