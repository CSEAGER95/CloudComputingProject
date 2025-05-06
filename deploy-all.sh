#!/bin/bash
# deploy-all.sh

set -e

echo "Starting full deployment process..."

# 1. Backend deployment
echo "Deploying backend to App Engine..."
cd backend
mvn clean package appengine:deploy -DskipTests
cd ..

# 2. Frontend deployment
echo "Building and deploying frontend to Cloud Run..."
cd frontend
docker build -t gcr.io/teamprojectmccewenseager/mcseager-frontend:latest \
  --build-arg REACT_APP_API_URL=https://teamprojectmccewenseager.ue.r.appspot.com .
docker push gcr.io/teamprojectmccewenseager/mcseager-frontend:latest

gcloud run deploy mcseager-frontend \
  --image gcr.io/teamprojectmccewenseager/mcseager-frontend:latest \
  --platform managed \
  --region us-east1 \
  --allow-unauthenticated
cd ..

echo "Deployment complete!"