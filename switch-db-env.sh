#!/bin/bash

# Function to switch to development environment
switch_to_dev() {
    sed -i '' 's|SPRING_DATASOURCE_URL=.*|SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/unihub|g' .env
    sed -i '' 's|SPRING_DATASOURCE_USERNAME=.*|SPRING_DATASOURCE_USERNAME=root|g' .env
    sed -i '' 's|SPRING_DATASOURCE_PASSWORD=.*|SPRING_DATASOURCE_PASSWORD=root1234|g' .env
    echo "Switched to development database configuration"
}

# Function to switch to production environment
switch_to_prod() {
    # Railway MySQL configuration
    sed -i '' 's|SPRING_DATASOURCE_URL=.*|SPRING_DATASOURCE_URL=jdbc:mysql://centerbeam.proxy.rlwy.net:36786/railway|g' .env
    sed -i '' 's|SPRING_DATASOURCE_USERNAME=.*|SPRING_DATASOURCE_USERNAME=root|g' .env
    sed -i '' 's|SPRING_DATASOURCE_PASSWORD=.*|SPRING_DATASOURCE_PASSWORD=zVkytGlPktoPlPYBjmAwHRnjVyXNbqzz|g' .env
    echo "Switched to production database configuration"
}

# Check if .env file exists
if [ ! -f .env ]; then
    echo "Error: .env file not found"
    exit 1
fi

# Check command line argument
if [ "$1" = "dev" ]; then
    switch_to_dev
elif [ "$1" = "prod" ]; then
    switch_to_prod
else
    echo "Usage: ./switch-db-env.sh [dev|prod]"
    echo "  dev  - Switch to development database configuration"
    echo "  prod - Switch to production database configuration"
    exit 1
fi 