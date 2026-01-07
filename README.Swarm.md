# Docker Swarm

## Option 1: Docker-in-Docker Containers (Recommended for local testing)

Create additional Docker engine containers that can act as worker nodes:

```
# Create a network for the swarm
docker network create --driver overlay swarm-network

# Initialize Swarm
docker swarm init

# Create worker nodes using DinD
docker run -d --privileged --name worker1 \
  --hostname worker1 \
  --network swarm-network \
  docker:dind

docker run -d --privileged --name worker2 \
  --hostname worker2 \
  --network swarm-network \
  docker:dind

# Get the join token from your manager
docker swarm join-token worker

# Join workers to the swarm (execute inside each worker container)
docker exec worker1 docker swarm join --token <WORKER_TOKEN> <MANAGER_IP>:2377
docker exec worker2 docker swarm join --token <WORKER_TOKEN> <MANAGER_IP>:2377
```

## Option 2: Using Docker Compose (Easier)

Create a docker-compose.dind.yaml:

```
version: '3.8'

services:
  manager:
    image: docker:dind
    privileged: true
    hostname: manager
    ports:
      - "2377:2377"
      - "7946:7946"
      - "4789:4789"
    command: dockerd
    
  worker1:
    image: docker:dind
    privileged: true
    hostname: worker1
    command: dockerd
    
  worker2:
    image: docker:dind
    privileged: true
    hostname: worker2
    command: dockerd
```

SWARM INITIALIZATION

```
echo "postgres" | docker secret create db-password -
echo "people" | docker secret create api-db-password -
echo "keycloak" | docker secret create keycloak-db-password -
echo "admin" | docker secret create keycloak-admin-password -

docker config create nginx-conf ./nginx/nginx.conf
docker config create nginx-default-conf ./nginx/conf.d/default.conf
docker config create nginx-cert-crt ./nginx/certs/mcqueide.local.crt
docker config create nginx-cert-key ./nginx/certs/mcqueide.local.key

docker node update --label-add database=true docker-desktop

docker stack deploy -c compose.swarm.yaml people

docker stack services people

docker service logs -f people_db

docker service scale people_keycloak=1

docker service logs -f people_keycloak
```