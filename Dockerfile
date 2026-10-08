# Stage 1: Build Angular Frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install --legacy-peer-deps
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot Backend
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
COPY backend/src ./src

# FIX: Copy files directly from where Angular outputs them (/app/backend/src/main/resources/static)
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /app/backend/src/main/resources/static/ src/main/resources/static/

RUN mvn clean package -DskipTests

# Stage 3: Lightweight Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]