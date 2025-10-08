#!/bin/bash
set -e

# export all variables from .env file
set -a
source .env
set +a

# ==============================
# Check and create Docker network
# ==============================
if ! docker network ls | grep -q "tokenus-network"; then
  echo "Creating tokenus-network..."
  docker network create tokenus-network
else
  echo "tokenus-network already exists."
fi

# Login to AWS ECR
aws ecr get-login-password --region $AWS_REGION | docker login \
  --username AWS \
  --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

# Pull latest image from ECR
echo "Pulling latest image from ECR..."
if ! docker pull $APP_IMAGE; then
    echo "Failed to pull image."
    exit 1
fi

# Docker compose down
echo "Docker compose down "
docker compose down --remove-orphans

# Create volume if not exists
docker volume create mysql_data || true

# Docker compose up
echo "Docker compose up"
if ! docker compose up -d; then
    echo "Failed to start containers."
    exit 1
fi

# Removing dangling images
echo "Removing dangling images..."
docker image prune -f

echo "Removing stopped containers..."
docker container prune -f

for i in {1..10}; do
    if [ "$i" -eq 10 ]; then
       echo "Health check failed"
       docker compose down
       exit 1
    fi

    if curl "http://localhost:8080/health"; then
        echo "Container is running healthy..."
        break
    fi

    echo "Spring Boot application health check in progress..."
    sleep 15
done

echo "All tasks are completed."