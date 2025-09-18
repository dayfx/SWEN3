FROM maven:3.9.11-eclipse-temurin-21 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD .. $HOME
RUN mvn package -DskipTests

FROM openjdk:21
COPY --from=build /usr/app/PaperlessREST/target/*.jar /app/runner.jar
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]
