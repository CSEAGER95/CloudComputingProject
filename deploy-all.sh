#!/bin/bash
# Improved deploy-all.sh

# Exit on any error
set -e

echo "===== Starting comprehensive deployment process ====="

# Project configuration
PROJECT_ID="teamprojectmccewenseager"
FRONTEND_SERVICE="mcseager-frontend"
REGION="us-east1"
API_URL="https://teamprojectmccewenseager.ue.r.appspot.com"

# Make sure gcloud is configured with the correct project
echo "Setting project to $PROJECT_ID..."
gcloud config set project $PROJECT_ID

# 1. Deploy backend to App Engine
echo -e "\n===== Deploying backend to App Engine ====="
cd backend
mvn clean package appengine:deploy -DskipTests -Dapp.deploy.projectId=$PROJECT_ID
echo "Backend deployment complete"
cd ..

# Wait for backend to fully initialize
echo "Waiting for backend to initialize (30 seconds)..."
sleep 30

# 2. Test backend connectivity
echo -e "\n===== Verifying backend API is accessible ====="
curl -s -o /dev/null -w "%{http_code}" "$API_URL/prompt/test"
if [ $? -ne 0 ]; then
  echo "WARNING: Backend API test failed, but continuing with deployment"
else
  echo "Backend API is accessible"
fi

# 3. Deploy frontend to Cloud Run
echo -e "\n===== Building and deploying frontend to Cloud Run ====="
cd frontend

# Build optimized docker image
echo "Building frontend Docker image..."
docker build -t gcr.io/$PROJECT_ID/$FRONTEND_SERVICE:latest \
  --build-arg REACT_APP_API_URL=$API_URL \
  .

# Push to Container Registry
echo "Pushing image to Google Container Registry..."
docker push gcr.io/$PROJECT_ID/$FRONTEND_SERVICE:latest

# Deploy to Cloud Run
echo "Deploying to Cloud Run..."
gcloud run deploy $FRONTEND_SERVICE \
  --image gcr.io/$PROJECT_ID/$FRONTEND_SERVICE:latest \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --memory 512Mi \
  --cpu 1 \
  --min-instances 0 \
  --max-instances 2 \
  --set-env-vars "REACT_APP_API_URL=$API_URL"

# Return to project root directory
cd ..

# 4. Final verification
echo -e "\n===== Final Verification ====="

# Get the frontend URL
FRONTEND_URL=$(gcloud run services describe $FRONTEND_SERVICE --region=$REGION --format='value(status.url)')

echo "Backend API URL: $API_URL"
echo "Frontend URL: $FRONTEND_URL"

# Test backend health
echo -e "\nTesting backend health..."
BACKEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$API_URL/prompt/test")
if [ "$BACKEND_STATUS" = "200" ]; then
  echo "✅ Backend is healthy"
else
  echo "⚠️ Backend returned status code: $BACKEND_STATUS"
fi

# Test frontend
echo -e "\nTesting frontend..."
FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$FRONTEND_URL")
if [ "$FRONTEND_STATUS" = "200" ]; then
  echo "✅ Frontend is accessible"
else
  echo "⚠️ Frontend returned status code: $FRONTEND_STATUS"
fi

echo -e "\n===== Deployment Complete ====="
echo "Your application should be available at:"
echo "- Frontend: $FRONTEND_URL"
echo "- Backend API: $API_URL"
echo -e "\nTo test CORS configuration, run:"
echo "curl -X OPTIONS -H 'Origin: http://localhost:3000' -v $API_URL/prompt"
echo -e "\nTo view logs:"
echo "- Frontend: gcloud logging read 'resource.type=cloud_run_revision AND resource.labels.service_name=$FRONTEND_SERVICE'"
echo "- Backend: gcloud app logs tail"
echo -e "\nDeployment timestamp: $(date)"