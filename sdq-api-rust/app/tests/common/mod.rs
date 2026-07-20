use dotenvy::dotenv;
use sqlx::{Pool, Postgres};
use std::env;

pub async fn test_db_pool() -> Pool<Postgres> {
    dotenv().ok();

    let url =
        env::var("SDQ_TEST_DATABASE_URL").expect("SDQ_TEST_DATABASE_URL must be set for tests");

    Pool::<Postgres>::connect(&url)
        .await
        .expect("failed to connect to test db")
}
