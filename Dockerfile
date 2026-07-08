FROM eclipse-temurin:17-jdk-jammy
LABEL authors="karsl"


WORKDIR /src
COPY target/RaceToBid-0.0.1-SNAPSHOT.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-jar","RaceToBid-0.0.1-SNAPSHOT.jar"]