# ===== BUILDER STAGE =====
FROM rust:latest AS builder

# Create a new empty shell project
WORKDIR /app

# Pre-build dependencies (caching)
COPY Cargo.toml Cargo.lock ./
RUN mkdir src && echo "fn main() {}" > src/main.rs
RUN cargo build --release
RUN rm -rf src

# Copy actual source
COPY . .

# Build your real app
RUN cargo build --release


# ===== RUNTIME STAGE =====
FROM debian:bookworm-slim AS runtime

# Install minimal runtime dependencies
RUN apt-get update && apt-get install -y \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the compiled binary from builder
COPY --from=builder /app/target/release/sdq-api-rust /app/sdq-api-rust

# Copy static resources (your Vite dist/)
COPY --from=builder /app/dist /app/dist

# Expose your Axum port
EXPOSE 3000

# Run the app
CMD ["/app/sdq-api-rust"]

