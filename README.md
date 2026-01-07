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
- **Docker Swarm** - Native Docker orchestration for production
- **Kubernetes** - Container orchestration for production
- **Helm** - Kubernetes package manager
- **Nginx** - Reverse proxy and load balancer
- **GitHub Actions** - CI/CD automation
- **Docker Scout** - Container security scanning
- **Cosign** - Container image signing and verification
- **Docker Bake** - Advanced multi-platform builds

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
├── .github/workflows/           # GitHub Actions CI/CD pipelines
├── .k8s/                        # Kubernetes manifests
├── helm/                        # Helm charts
├── nginx/                       # Nginx configuration
├── docker-bake.hcl              # Docker Bake configuration
├── docker-compose.swarm.yaml    # Docker Swarm stack file
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

## 🐝 Production Deployment with Docker Swarm

Docker Swarm provides native Docker clustering and orchestration, ideal for:
- Simple production deployments without Kubernetes complexity
- Teams already familiar with Docker Compose
- High availability with built-in load balancing
- Rolling updates and service discovery

### Prerequisites

- Docker Engine with Swarm mode enabled
- Multi-node cluster (optional, can run on single node)

### Initialize Swarm

```bash
# On manager node
docker swarm init --advertise-addr <MANAGER-IP>

# On worker nodes (use token from init output)
docker swarm join --token <TOKEN> <MANAGER-IP>:2377

# Verify cluster
docker node ls
```

### Deploy the Stack

```bash
# Deploy all services (app, database, nginx)
docker stack deploy -c docker-compose.swarm.yaml people-api

# List services
docker service ls

# Check service status
docker service ps people-api_app

# Scale the application
docker service scale people-api_app=5

# View logs
docker service logs -f people-api_app
```

### Access the Application

```bash
# Application is available on any cluster node
curl http://<NODE-IP>/api/v1/people
```

### Stack Features

- **Replicas**: 3 app instances for high availability
- **Load Balancing**: Automatic load distribution across replicas
- **Rolling Updates**: Zero-downtime deployments
- **Health Checks**: Automatic container restart on failure
- **Secrets**: Encrypted credential management
- **Overlay Network**: Secure inter-service communication

### Managing the Stack

```bash
# Update service image
docker service update --image mcqueide/people-api:new-tag people-api_app

# Remove the stack
docker stack rm people-api

# Leave swarm (worker)
docker swarm leave

# Leave swarm (manager, force)
docker swarm leave --force
```

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
kubectl apply -f .k8s/backend/secret.yaml
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
kubectl rollout.undo deployment/people-api

# Uninstall Helm release
helm uninstall people-db
```

For detailed Kubernetes instructions, see `README.K8S.md`.

## 🔄 CI/CD with GitHub Actions

Automated CI/CD pipeline handles building, testing, scanning, signing, and deploying:

### Pipeline Features

- **Multi-stage builds** - Separate build and runtime stages
- **Multi-platform images** - Build for AMD64 and ARM64
- **Security scanning** - Docker Scout vulnerability analysis
- **Image signing** - Cosign keyless signing with GitHub OIDC
- **SBOM generation** - Software Bill of Materials for compliance
- **Automated deployment** - Deploy to staging/production environments

### Required Secrets

Configure these in GitHub repository settings:

```bash
DOCKERHUB_USERNAME    # Docker Hub username
DOCKERHUB_TOKEN       # Docker Hub access token
KUBE_CONFIG           # Kubernetes config (base64 encoded)
COSIGN_PRIVATE_KEY    # Cosign private key (optional)
```

### Verify Signed Images

```bash
# Verify image signature
cosign verify --certificate-identity-regexp="https://github.com/mcqueide/people-api" \
  --certificate-oidc-issuer=https://token.actions.githubusercontent.com \
  mcqueide/people-api:latest

# Inspect SBOM
cosign download sbom mcqueide/people-api:latest | jq
```

## 🔒 Security and Compliance

### Docker Scout

Continuous vulnerability scanning for container images:

```bash
# Scan local image
docker scout cves people-api:latest

# Compare with baseline
docker scout compare --to people-api:latest people-api:dev

# View recommendations
docker scout recommendations people-api:latest

# Generate SBOM
docker scout sbom people-api:latest
```

**Scout Integration:**
- Automated scanning in CI/CD pipeline
- Policy enforcement (no critical vulnerabilities)
- Real-time security advisories
- Compliance reporting

### Cosign - Image Signing

Sign and verify container images for supply chain security:

```bash
# Generate key pair (one-time setup)
cosign generate-key-pair

# Sign image
cosign sign --key cosign.key mcqueide/people-api:latest

# Verify signature
cosign verify --key cosign.pub mcqueide/people-api:latest

# Keyless signing (GitHub Actions OIDC)
cosign sign mcqueide/people-api:latest

# Attach SBOM
cosign attach sbom --sbom sbom.spdx mcqueide/people-api:latest
```

**Security Benefits:**
- Cryptographic proof of image authenticity
- Supply chain attack prevention
- Compliance with security policies
- Transparent image provenance

## 🏗️ Advanced Builds with Docker Bake

Docker Bake enables complex multi-platform builds with a single configuration:

### Building with Bake

```bash
# Build all targets
docker buildx bake

# Build specific target
docker buildx bake app

# Build and push
docker buildx bake --push

# Override variables
TAG=v1.2.3 docker buildx bake --push

# Build for specific platform
docker buildx bake --set app.platform=linux/arm64

# Use remote definition
docker buildx bake https://github.com/mcqueide/people-api.git
```

### Bake Benefits

- **Multi-target builds** - Build dev and prod images simultaneously
- **Multi-platform** - ARM64 and AMD64 in single command
- **Build matrix** - Test multiple configurations
- **Shared cache** - Faster builds with layer caching
- **Dependency management** - Define build dependencies
- **GitOps friendly** - Version-controlled build configuration

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
- **Image Scanning**: Docker Scout integration for vulnerability detection
- **Image Signing**: Cosign signatures for supply chain security
- **SBOM**: Software Bill of Materials for compliance and auditing

## 📊 Monitoring

- **Health Checks**: Available at `/actuator/health`
- **Metrics**: Spring Boot Actuator endpoints exposed
- **Logs**: Structured JSON logging with rotation

## 📝 License

This project is a demo application for Docker and Kubernetes learning purposes.

## 👤 Author

mcqueide