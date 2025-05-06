#!/bin/sh
# Replace $PORT in the nginx config template
envsubst '${PORT}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf

# Remove the default nginx config that might be causing conflicts
rm -f /etc/nginx/conf.d/default.conf.template

# Check if the config is valid
nginx -t

# Start nginx in foreground
exec nginx -g 'daemon off;'