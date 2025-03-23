FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY . /app

RUN /gradlew clean build -x test

FROM openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/email-service.jar

EXPOSE 8081

CMD ["java", "-jar", "email-service.jar"]
