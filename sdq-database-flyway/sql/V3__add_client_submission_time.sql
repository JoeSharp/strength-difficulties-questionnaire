ALTER TABLE client ADD COLUMN submitted_at TIMESTAMPTZ;
ALTER TABLE client ALTER COLUMN submitted_at SET DEFAULT CURRENT_TIMESTAMP;

CREATE OR REPLACE VIEW client_full AS
WITH client AS (
    SELECT * FROM client
),
intervention_types AS (
    SELECT
        it.client_id,
        jsonb_agg(
            jsonb_build_object(
                'type', it.intervention_type,
                'sessions', it.sessions
            )
        ) AS interventions
    FROM intervention_type it
    GROUP BY it.client_id
),
disability_types AS (
    SELECT
        dt.client_id,
        jsonb_agg(dt.disability_type) AS disability_types
    FROM disability_type dt
    GROUP BY dt.client_id
),
aces AS (
    SELECT
        a.client_id,
        jsonb_object_agg(a.ace_type, a.score) AS aces
    FROM aces a
    GROUP BY a.client_id
)
SELECT
    c.client_id,
    c.code_name,
    c.date_of_birth,
    c.gender,
    c.council,
    c.ethnicity,
    c.eal,
    c.disability_status,
    c.care_experience,
    c.funding_source,
    it.interventions,
    dt.disability_types,
    a.aces,
    c.submitted_at
FROM client c
LEFT JOIN intervention_types it ON it.client_id = c.client_id
LEFT JOIN disability_types dt ON dt.client_id = c.client_id
LEFT JOIN aces a ON a.client_id = c.client_id;
