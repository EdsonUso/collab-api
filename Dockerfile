FROM eclipse-temurin:21-jre-alpine
LABEL authors="Edson Cruz"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

COPY target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
