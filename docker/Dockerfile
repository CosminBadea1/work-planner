FROM maven:3.9.6-amazoncorretto-21-debian as builder

RUN groupadd --system --gid 1000 test
RUN useradd --system --gid test --uid 1000 --shell /bin/bash --create-home test

USER test
WORKDIR '/home/test'

COPY . .

RUN ["mvn", "clean", "package"]

FROM amazoncorretto:21-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /home/test/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app.jar"]
