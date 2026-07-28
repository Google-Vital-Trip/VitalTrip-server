FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
USER 1000
ENTRYPOINT ["java", "-jar", "app.jar"]
