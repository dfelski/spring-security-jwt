FROM gradle:5.4.1-jre11-slim as builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean bootJar

FROM azul/zulu-openjdk-alpine:11.0.2
COPY --from=builder /home/gradle/src/build/libs/application.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]