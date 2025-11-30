FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar cloud-file-storage.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cloud-file-storage.jar"]