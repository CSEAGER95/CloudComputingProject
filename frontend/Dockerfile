FROM node:18-alpine as build
WORKDIR /app

# Copy .env file first
COPY .env .

# Then continue with the rest
COPY package*.json ./
RUN npm install
COPY . ./

# Use the environment variable
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL
RUN echo "Building with API URL: $REACT_APP_API_URL" && npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/templates/default.conf.template
ENV PORT 8080
EXPOSE 8080

# Add script to properly handle Cloud Run startup
COPY --from=build /app/start-nginx.sh /
RUN chmod +x /start-nginx.sh
CMD ["/start-nginx.sh"]