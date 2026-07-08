FROM openjdk:21-jdk
LABEL authors="karsl"


WORKDIR /src
COPY target/RaceToBid-0.0.1-SNAPSHOT.jar .
EXPOSE 801
ENTRYPOINT ["java", "-jar","RaceToBid-0.0.1-SNAPSHOT.jar"]