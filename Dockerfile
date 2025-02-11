FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 9192
ENTRYPOINT ["java", "-jar", "app.jar"]
