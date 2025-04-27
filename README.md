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

## Test Reports

The project is configured to generate detailed test reports using JUnit and JaCoCo for code coverage. These reports provide insights into test execution results and code coverage metrics.

### Generating Test Reports

To generate test reports, run the provided script:

```bash
# Make the script executable
chmod +x scripts/generate-test-reports.sh

# Run the script
./scripts/generate-test-reports.sh
```

Alternatively, you can run the Maven command directly:

```bash
mvn clean test
```

### Viewing Test Reports

After running the tests, the following reports are available:

1. **JUnit Test Reports**:
   - XML reports: `target/surefire-reports/*.xml`
   - Text reports: `target/surefire-reports/*.txt`

2. **JaCoCo Code Coverage Reports**:
   - HTML report: `target/site/jacoco/index.html` (open in a browser)
   - XML report: `target/site/jacoco/jacoco.xml`
   - CSV report: `target/site/jacoco/jacoco.csv`

The JaCoCo HTML report provides a user-friendly interface to explore code coverage metrics, including:
- Line coverage
- Branch coverage
- Method coverage
- Class coverage

### Continuous Integration

These test reports are particularly useful in CI/CD pipelines to track test results and code quality over time. The project's GitHub Actions workflow can be configured to publish these reports as artifacts.

## UML Diagrams

The project includes UML diagrams to help understand the application architecture and behavior. These diagrams are located in the `src/main/resources/uml/` directory.

### Available Diagrams

1. **Class Diagram** (`class-diagram.puml`): Shows the main classes, their attributes, methods, and relationships.
2. **Sequence Diagram** (`sequence-diagram.puml`): Illustrates the order creation process and the interactions between components.
3. **State Diagram** (`state-diagram.puml`): Shows the possible states of an order and the transitions between them.

### How to View the Diagrams

To view the diagrams, you need to use a PlantUML renderer. Here are some options:

1. **Online PlantUML Editor**: Copy the content of the `.puml` file and paste it into the [PlantUML Online Editor](http://www.plantuml.com/plantuml/uml/).
2. **IntelliJ IDEA**: Install the PlantUML plugin and view the diagrams directly in the IDE.
3. **VS Code**: Install the PlantUML extension and view the diagrams directly in the editor.
4. **Command Line**: Use the PlantUML command-line tool to generate images from the PlantUML files.

For more detailed information about the diagrams, please refer to the [UML README](src/main/resources/uml/README.md).
