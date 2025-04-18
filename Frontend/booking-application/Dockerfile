FROM node:18-alpine AS build

LABEL maintainer="Saksham"

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# Serve the app using Nginx
FROM nginx:1.25-alpine

LABEL maintainer="Saksham"

# Remove default Nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy built React app from previous stage
COPY --from=build /app/build /usr/share/nginx/html

# Custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the port Nginx listens on
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
