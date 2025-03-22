FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY . /app

RUN gradle clean build -x test

FROM openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/email-service.jar /app/email-service.jar

EXPOSE 8081

CMD ["java", "-jar", "email-service.jar"]
