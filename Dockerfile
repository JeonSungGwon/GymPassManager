FROM openjdk:11

WORKDIR /Toy-project

COPY Toy_project-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]