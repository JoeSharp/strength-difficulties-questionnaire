package db

import (
	"context"
	"encoding/json"
	"log"
	"time"

	"github.com/google/uuid"

	"github.com/jackc/pgx/v5/pgtype"
	"github.com/jackc/pgx/v5/pgxpool"
	domain "joesharpcs.co.uk/sdq/domain"

	"time"
)

type PgxClientRepository struct {
	DB *pgxpool.Pool
}

func (r *PgxClientRepository) GetClients() ([]domain.SdqClient, error) {
	rows, err := r.DB.Query(context.Background(),
		`SELECT client_id, code_name, date_of_birth, gender, council, ethnicity, eal,
		        disability_status, disability_types, care_experience, interventions, aces,
		        funding_source, submitted_at
		 FROM client_full`,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var out []domain.SdqClient

	for rows.Next() {
		var c domain.SdqClient
		var dateOfBirth *time.Time
		var submittedAt pgtype.Timestamptz
		var disabilityTypes, interventions, aces []byte
		if err := rows.Scan(
			&c.ID,
			&c.CodeName,
			&dateOfBirth,
			&c.Gender,
			&c.Council,
			&c.Ethnicity,
			&c.EnglishAdditionalLanguage,
			&c.DisabilityStatus,
			&disabilityTypes,
			&c.CareExperience,
			&interventions,
			&aces,
			&c.FundingSource,
			&submittedAt,
		); err != nil {
			return nil, err
		}
		if submittedAt.Valid {
			submittedAtUTC := submittedAt.Time.UTC()
			c.SubmittedAt = &submittedAtUTC
		}
		if dateOfBirth != nil {
			c.DateOfBirth = dateOfBirth.Format("2006-01-02")
		}
		if len(disabilityTypes) > 0 {
			if err := json.Unmarshal(disabilityTypes, &c.DisabilityTypes); err != nil {
				return nil, err
			}
		}
		if len(interventions) > 0 {
			if err := json.Unmarshal(interventions, &c.Interventions); err != nil {
				return nil, err
			}
		}
		if len(aces) > 0 {
			if err := json.Unmarshal(aces, &c.Aces); err != nil {
				return nil, err
			}
		}
		out = append(out, c)
	}

	return out, rows.Err()
}

func (r *PgxClientRepository) CreateClient(client domain.SdqClient) (domain.SdqClient, error) {
	dateOfBirth, err := time.Parse("2006-01-02", client.DateOfBirth)
	if err != nil {
		return domain.SdqClient{}, err
	}

	ctx := context.Background()
	tx, err := r.DB.Begin(ctx)
	if err != nil {
		return domain.SdqClient{}, err
	}
	defer tx.Rollback(ctx)

	client.ID = uuid.New()
	var submittedAt pgtype.Timestamptz
	err = tx.QueryRow(ctx,
		`INSERT INTO client (
			client_id, code_name, date_of_birth, gender, council, ethnicity, eal,
			disability_status, care_experience, funding_source
		) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
		RETURNING submitted_at`,
		client.ID,
		client.CodeName,
		dateOfBirth,
		client.Gender,
		client.Council,
		client.Ethnicity,
		client.EnglishAdditionalLanguage,
		client.DisabilityStatus,
		client.CareExperience,
		client.FundingSource,
	).Scan(&submittedAt)
	if err != nil {
		return domain.SdqClient{}, err
	}
	if submittedAt.Valid {
		submittedAtUTC := submittedAt.Time.UTC()
		client.SubmittedAt = &submittedAtUTC
	}

	for _, disabilityType := range client.DisabilityTypes {
		if _, err := tx.Exec(ctx,
			"INSERT INTO disability_type (client_id, disability_type) VALUES ($1, $2)",
			client.ID,
			disabilityType,
		); err != nil {
			return domain.SdqClient{}, err
		}
	}
	for _, intervention := range client.Interventions {
		if _, err := tx.Exec(ctx,
			"INSERT INTO intervention_type (client_id, intervention_type, sessions) VALUES ($1, $2, $3)",
			client.ID,
			intervention.Type,
			intervention.Sessions,
		); err != nil {
			return domain.SdqClient{}, err
		}
	}
	for aceType, score := range client.Aces {
		if _, err := tx.Exec(ctx,
			"INSERT INTO aces (client_id, ace_type, score) VALUES ($1, $2, $3)",
			client.ID,
			aceType,
			score,
		); err != nil {
			return domain.SdqClient{}, err
		}
	}
	if err := tx.Commit(ctx); err != nil {
		return domain.SdqClient{}, err
	}
	return client, nil
}

func CreateDb(db_url string) domain.SdqClientRepository {
	pool, err := pgxpool.New(context.Background(), db_url)
	if err != nil {
		log.Fatalf("unable to connect to database: %v", err)
	}

	if err := pool.Ping(context.Background()); err != nil {
		log.Fatalf("unable to connect to database: %v", err)
	}

	return &PgxClientRepository{DB: pool}
}
