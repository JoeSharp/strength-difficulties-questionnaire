package domain

import (
	"time"

	"github.com/google/uuid"
)

type SdqClient struct {
	ID                        uuid.UUID      `json:"id"`
	CodeName                  string         `json:"codeName"`
	DateOfBirth               string         `json:"dateOfBirth,omitempty"`
	Gender                    *string        `json:"gender,omitempty"`
	Council                   *string        `json:"council,omitempty"`
	Ethnicity                 *string        `json:"ethnicity,omitempty"`
	EnglishAdditionalLanguage *string        `json:"englishAdditionalLanguage,omitempty"`
	DisabilityStatus          *string        `json:"disabilityStatus,omitempty"`
	DisabilityTypes           []string       `json:"disabilityTypes,omitempty"`
	CareExperience            *string        `json:"careExperience,omitempty"`
	Interventions             []Intervention `json:"interventions,omitempty"`
	Aces                      map[string]int `json:"aces,omitempty"`
	FundingSource             *string        `json:"fundingSource,omitempty"`
	SubmittedAt               *time.Time     `json:"submittedAt,omitempty"`
}

type Intervention struct {
	Type     string `json:"type"`
	Sessions int    `json:"sessions"`
}

type Goal struct {
	ID          uuid.UUID `json:"id"`
	ClientID    uuid.UUID `json:"clientId"`
	Type        *string   `json:"type,omitempty"`
	Description *string   `json:"description,omitempty"`
}

type SdqClientRepository interface { //creates the "interface" to be interacted with?
	GetClients() ([]SdqClient, error)
	CreateClient(client SdqClient) (SdqClient, error)
	GetGoals(clientId string) ([]Goal, error)
	CreateGoal(goal Goal) (Goal, error)
}

type SdqApp struct {
	ClientRepository SdqClientRepository
}
