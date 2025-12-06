# People API

A RESTful API for managing people records built with modern Java technologies and containerized deployment options.

## 🚀 Technologies

- **Java 21** - Latest LTS version with modern language features
- **Spring Boot 3.5.7** - Enterprise-grade application framework
- **Maven** - Dependency management and build tool
- **PostgreSQL 17** - Relational database
- **Flyway** - Database migration management
- **MapStruct** - Type-safe bean mapping
- **Docker** - Container runtime for development
- **Kubernetes** - Container orchestration for production
- **Helm** - Kubernetes package manager
- **Nginx** - Reverse proxy and load balancer

## 📋 Features

- RESTful CRUD operations for person entities
- Clean architecture with hexagonal/ports-and-adapters pattern
- Database migrations with Flyway
- OpenAPI/Swagger documentation
- Health checks and actuator endpoints
- Containerized deployment
- Production-ready Kubernetes manifests

## 🏗️ Project Structure

```
├── src/
│   ├── main/java/com/mcqueide/dockertest/
│   │   ├── application/         # Application services
│   │   ├── domain/              # Domain models and ports
│   │   ├── infrastructure/      # Infrastructure adapters
│   │   └── interfaces/          # REST controllers and DTOs
│   └── main/resources/
│       ├── db/migration/        # Flyway database migrations
│       └── application.yml      # Application configuration
├── .k8s/                        # Kubernetes manifests
├── helm/                        # Helm charts
├── nginx/                       # Nginx configuration
├── Dockerfile                   # Production container image
├── Dockerfile-dev               # Development container image
└── compose.yaml                 # Docker Compose for production
```

## 🔧 Building the Project

### Prerequisites

- Java 21 or later
- Maven 3.9+ (or use included Maven wrapper)
- Docker (for containerized development)
- Kubernetes cluster (for production deployment)

### Local Build

```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Or with installed Maven
mvn clean package
```

### Running Tests

```bash
./mvnw test
```

The project includes:
- Unit tests with Mockito
- Integration tests with Testcontainers

## 🐳 Development with Docker

Docker is the recommended approach for local development as it provides:
- Consistent development environment
- Easy database setup
- Hot reload support
- No local Java/Maven installation required

### Quick Start

1. **Start the development environment:**

```bash
docker compose -f compose-dev.yaml up
```

This starts:
- PostgreSQL database on port `5432`
- Spring Boot app on port `8080` with hot reload
- Remote debugging on port `5005`

2. **Access the application:**

- API: http://localhost:8080/api/v1/people
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

3. **Stop the environment:**

```bash
docker compose -f compose-dev.yaml down
```

### Development Features

- **Hot Reload**: Changes to source code trigger automatic restart
- **Debug Mode**: Connect your IDE debugger to port `5005`
- **Persistent Data**: Database data persists in Docker volume
- **Live Logs**: View application logs with `docker compose logs -f app`

### Production-like Environment

To test the production build locally:

```bash
# Build and start all services (app, database, nginx)
docker compose up --build

# Scale the application (2 replicas behind nginx)
docker compose up -d --scale app=2
```

Access through Nginx reverse proxy:
- Application: http://localhost:80/api/v1/people
- Health Check: http://localhost/health

For more Docker deployment options, see `README.Docker.md`.

## ☸️ Production Deployment with Kubernetes

Kubernetes is used for production deployments, providing:
- High availability with multiple replicas
- Load balancing
- Rolling updates with zero downtime
- Auto-scaling capabilities
- Resource management

### Prerequisites

- Kubernetes cluster (local or cloud)
- `kubectl` configured
- Helm 3.x installed

For local testing, you can use [Kind](https://kind.sigs.k8s.io/):

```bash
kind create cluster --config=.k8s/kind.yaml --name=people-api
kubectl cluster-info --context kind-people-api
```

### Deploying with Helm (Recommended)

1. **Deploy PostgreSQL database:**

```bash
# Install the PostgreSQL chart
helm install people helm/postgres

# Verify deployment
kubectl get pods
kubectl get svc
```

2. **Deploy the application:**

```bash
# Apply Kubernetes manifests
kubectl apply -f .k8s/backend/env.yaml
kubectl apply -f .k8s/backend/secrect.yaml
kubectl apply -f .k8s/backend/deployment.yaml
kubectl apply -f .k8s/backend/service.yaml

# Verify deployment
kubectl get deployments
kubectl get pods
kubectl get svc
```

3. **Access the application:**

```bash
# Port forward to access locally
kubectl port-forward svc/people-api 8080:8080

# Access at http://localhost:8080/api/v1/people
```

### Deployment Details

The Kubernetes deployment includes:

- **Application**: 3 replicas for high availability
- **Database**: StatefulSet with persistent storage
- **Resources**: CPU and memory limits/requests configured
- **Probes**: Startup, readiness, and liveness checks
- **Security**: Non-root user, dropped capabilities, security contexts

### Helm Chart Configuration

Customize the PostgreSQL deployment by editing `helm/postgres/values.yaml`:

```yaml
container:
  resources:
    requests:
      cpu: "500m"
      memory: "512Mi"
    limits:
      cpu: "1"
      memory: "1Gi"
```

### Useful Commands

```bash
# View application logs
kubectl logs -f deployment/people-api

# View database logs
kubectl logs -f deployment/people-db

# Scale application
kubectl scale deployment/people-api --replicas=5

# Update deployment
kubectl set image deployment/people-api people-api=mcqueide/people-api:new-tag

# Check deployment status
kubectl rollout status deployment/people-api

# Rollback deployment
kubectl rollout undo deployment/people-api

# Uninstall Helm release
helm uninstall people-db
```

For detailed Kubernetes instructions, see `README.K8S.md`.

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/people` | Create a new person |
| GET | `/api/v1/people/{id}` | Get person by ID |
| PUT | `/api/v1/people/{id}` | Update person |
| DELETE | `/api/v1/people/{id}` | Delete person |
| GET | `/api/v1/people` | List people (paginated) |

### Example Request

```bash
# Create a person
curl -X POST http://localhost:8080/api/v1/people \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Grace Hopper",
    "birthday": "1906-12-09",
    "address": "New York",
    "phone": "+1-555-1234"
  }'

# Get all people
curl http://localhost:8080/api/v1/people?page=0&size=20
```

## 🔒 Security

- **Secrets Management**: Database credentials stored in Kubernetes Secrets
- **Network Policies**: Backend network isolated from external access
- **Security Contexts**: Containers run as non-root with dropped capabilities
- **Resource Limits**: CPU and memory limits prevent resource exhaustion

## 📊 Monitoring

- **Health Checks**: Available at `/actuator/health`
- **Metrics**: Spring Boot Actuator endpoints exposed
- **Logs**: Structured JSON logging with rotation

## 📝 License

This project is a demo application for Docker and Kubernetes learning purposes.

## 👤 Author

mcqueide