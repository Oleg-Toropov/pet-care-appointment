FROM openjdk:17-jdk-slim
WORKDIR /app

RUN apt-get update && apt-get install -y curl

COPY target/*.jar app.jar
EXPOSE 9192
ENTRYPOINT ["java", "-jar", "app.jar"]
