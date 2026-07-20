use sdq_db::client_service::ClientServiceSqlxImpl;
use sdq_model::DemographicField;
use sdq_service::ClientService;

mod common;

use crate::common::test_db_pool;

#[tokio::test]
async fn test_demographic_report() {
    let pool = test_db_pool().await;
    let service = ClientServiceSqlxImpl::new(pool);

    // seed test data
    sqlx::query("INSERT INTO client (ethnicity) VALUES ('White')")
        .execute(&service.pool)
        .await
        .unwrap();

    let report = service
        .get_demographic_report(DemographicField::Ethnicity)
        .await
        .unwrap();

    assert_eq!(report.counts[0].option, "White");
}
