FROM openjdk:17-oracle

VOLUME /tmp

RUN mvn package -DskipTests

COPY target/*.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "/app.jar"]