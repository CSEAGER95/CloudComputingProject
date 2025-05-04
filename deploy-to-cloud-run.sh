#!/bin/bash

# Exit on any error
set -e

# Configuration
PROJECT_ID="teamprojectmccewenseager"
REGION="us-east1"
SERVICE_NAME="mcseager-frontend"
IMAGE_NAME="gcr.io/$PROJECT_ID/$SERVICE_NAME:latest"

# Build the Docker image
echo "Building Docker image..."
docker build -t $IMAGE_NAME ./frontend

# Push the image to Google Container Registry
echo "Pushing image to Google Container Registry..."
docker push $IMAGE_NAME

# Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy $SERVICE_NAME \
  --image $IMAGE_NAME \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --project $PROJECT_ID \
  --set-env-vars "REACT_APP_API_URL=https://teamprojectmccewenseager.ue.r.appspot.com" \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 2 

echo "Deployment complete! Your application should be available soon at:"
gcloud run services describe $SERVICE_NAME --platform managed --region $REGION --format 'value(status.url)'