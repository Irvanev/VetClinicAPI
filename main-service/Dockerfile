FROM gradle:8-jdk17 AS build
WORKDIR /app

COPY . /app

RUN gradle clean build -x test

FROM openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/main-service.jar

EXPOSE 8080

CMD ["java", "-jar", "main-service.jar"]
