FROM node:18-alpine as build
WORKDIR /app

# Copy package files first for better caching
COPY package*.json ./
RUN npm install

# Copy all source files
COPY . ./

# Set the API URL explicitly using build arg
ARG REACT_APP_API_URL=https://teamprojectmccewenseager.ue.r.appspot.com
ENV REACT_APP_API_URL=$REACT_APP_API_URL

# Log the URL being used
RUN echo "Building with API URL: $REACT_APP_API_URL"

# Build the app with the environment variable
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

# Add a startup script that injects the API URL into the app at runtime
RUN echo '#!/bin/sh \n \
    echo "window.API_URL=\"'$REACT_APP_API_URL'\";" > /usr/share/nginx/html/config.js \n \
    nginx -g "daemon off;"' > /docker-entrypoint.sh && \
    chmod +x /docker-entrypoint.sh

CMD ["/docker-entrypoint.sh"]