
FROM openjdk-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

#FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=target/atlantique-0.0.1-SNAPSHOT.jar

COPY --from=build /app/${JAR_FILE} app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "/app.jar"]