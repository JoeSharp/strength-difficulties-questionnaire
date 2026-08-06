use sdq_service::parser::ParserService;
use sdq_xslx::parser_service::ParserServiceXslxImpl;

#[tokio::test]
async fn test_xslx_parser() {
    let service = ParserServiceXslxImpl::new();
    let bytes = std::fs::read("tests/data/Test File 1.xlsx").expect("failed to load test file");
    let parsed = service
        .parse_file("test_file".to_string(), bytes)
        .expect("Failed to parse test file");
}
