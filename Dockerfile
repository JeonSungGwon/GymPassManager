FROM openjdk:11-jre-slim

ARG JAR_FILE=build/libs/Toy-project-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

CMD ["java", "-jar", "/app.jar"]