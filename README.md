# Homely Meals

A Spring Boot application for managing meal orders and inventory.

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Maven (for local development)

## Docker Setup

This project includes Docker configuration for easy deployment.

### Using Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
# Set required environment variables
export AWS_SECRET_KEY=your_aws_secret_key
export AWS_ACCESS_KEY=your_aws_access_key
export JWT_SECRET=your_jwt_secret

# Build and start the application with PostgreSQL
docker-compose up -d
```

This will:
1. Build the application container
2. Start a PostgreSQL database
3. Connect the application to the database
4. Expose the application on port 8080

### Using Dockerfile Directly

If you prefer to run the application without Docker Compose:

```bash
# Build the Docker image
docker build -t homely-meals .

# Run the container
docker run -p 8080:8080 \
  -e DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/homely \
  -e DATASOURCE_USERNAME=postgres \
  -e DATASOURCE_PWD=postgres \
  -e AWS_SECRET_KEY=your_aws_secret_key \
  -e AWS_ACCESS_KEY=your_aws_access_key \
  -e JWT_SECRET=your_jwt_secret \
  homely-meals
```

## Environment Variables

The application requires the following environment variables:

| Variable | Description | Default in Docker |
|----------|-------------|------------------|
| DATASOURCE_URL | JDBC URL for PostgreSQL | jdbc:postgresql://db:5432/homely |
| DATASOURCE_USERNAME | Database username | postgres |
| DATASOURCE_PWD | Database password | postgres |
| AWS_SECRET_KEY | AWS Secret Key | - |
| AWS_ACCESS_KEY | AWS Access Key | - |
| JWT_SECRET | Secret for JWT tokens | - |

## Accessing the Application

Once running, the application is available at:
- http://localhost:8080

## Troubleshooting

- **Database Connection Issues**: Ensure PostgreSQL is running and accessible
- **AWS Service Issues**: Verify AWS credentials are correct
- **Container Won't Start**: Check logs with `docker logs <container_id>`
