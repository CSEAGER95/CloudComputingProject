# # Build stage
# FROM node:18-alpine as build

# # Set working directory
# WORKDIR /app

# # Copy package.json and package-lock.json
# COPY package*.json ./

# # Install dependencies
# RUN npm install

# # Copy all files
# COPY . ./

# # Set the API URL environment variable during build
# ARG REACT_APP_API_URL
# ENV REACT_APP_API_URL=${REACT_APP_API_URL}

# # Build the app
# RUN npm run build

# # Production stage
# FROM nginx:alpine

# # Copy built files from build stage to nginx serve directory
# COPY --from=build /app/build /usr/share/nginx/html

# # Copy nginx configuration if you have custom config
# # COPY nginx.conf /etc/nginx/conf.d/default.conf

# # Expose port 80
# EXPOSE 8080

# # Start nginx
# CMD ["nginx", "-g", "daemon off;"]

FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . ./
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=$REACT_APP_API_URL
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/configfile.template
ENV PORT 8080
EXPOSE 8080
CMD sh -c "envsubst '\$PORT' < /etc/nginx/conf.d/configfile.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"