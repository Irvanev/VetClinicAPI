FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /VetClinicAPI

COPY pom.xml /VetClinicAPI/
COPY src /VetClinicAPI/src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /VetClinicAPI

COPY --from=build /VetClinicAPI/target/*.jar VetClinicAPI.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "VetClinicAPI.jar"]
