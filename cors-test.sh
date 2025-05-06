#!/bin/bash
# cors-test.sh - Test CORS configuration for your API

API_URL="https://teamprojectmccewenseager.ue.r.appspot.com"
TEST_ORIGINS=(
  "https://mcseager-frontend-236280442449.us-east1.run.app"
  "http://localhost:3000"
  "https://example.com"
)

echo "===== CORS Configuration Test ====="
echo "Testing API: $API_URL"
echo ""

# Function to test CORS with different origins
test_cors() {
  local origin=$1
  local endpoint=$2
  
  echo "Origin: $origin"
  echo "Endpoint: $endpoint"
  
  # Perform OPTIONS request (preflight)
  echo "Testing OPTIONS request (preflight)..."
  curl -s -X OPTIONS \
    -H "Origin: $origin" \
    -H "Access-Control-Request-Method: GET" \
    -H "Access-Control-Request-Headers: Content-Type" \
    -I "$API_URL$endpoint" | grep -i "access-control"
    
  echo ""
  
  # Perform GET request
  echo "Testing GET request..."
  curl -s -X GET \
    -H "Origin: $origin" \
    -I "$API_URL$endpoint" | grep -i "access-control"
    
  echo -e "\n--------------------------------------\n"
}

# Test all origins against various endpoints
for origin in "${TEST_ORIGINS[@]}"; do
  test_cors "$origin" "/prompt/test"
  test_cors "$origin" "/prompt"
done

echo "===== Test Complete ====="
echo "If you see 'Access-Control-Allow-Origin: *' in the responses,"
echo "then your CORS configuration is working correctly."
echo ""
echo "If you don't see proper CORS headers or see errors,"
echo "check your backend configuration and ensure both the CorsFilter"
echo "and CORS configuration in RestfulApplication are deployed."