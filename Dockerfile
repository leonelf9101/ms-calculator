FROM gradle:7.4.0-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11.0.13-jre-slim
VOLUME /tmp
COPY --from=build /home/gradle/src/build/libs/ms-calculator-0.0.1-SNAPSHOT.jar ms-calculator.jar
ENTRYPOINT ["java", "-jar" , "/ms-calculator.jar"]