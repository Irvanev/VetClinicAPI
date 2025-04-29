FROM gradle:8-jdk17 AS build
WORKDIR /app

RUN mkdir -p logs

COPY . /app

RUN gradle clean build -x test

FROM openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/main-service-0.0.1-SNAPSHOT.jar /app/main-service.jar

EXPOSE 8080

CMD ["java", "-jar", "main-service.jar"]
