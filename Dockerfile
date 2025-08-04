FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-oracle

ARG JAR_FILE=target/atlantique-0.0.1-SNAPSHOT.jar

COPY --from=build /app/${JAR_FILE} app.jar

VOLUME /tmp

COPY --from=build /app/target/*.jar /app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "/app.jar"]
