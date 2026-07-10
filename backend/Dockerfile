FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY adapter-persistence/pom.xml adapter-persistence/
COPY adapter-web/pom.xml adapter-web/
COPY bootstrap/pom.xml bootstrap/
RUN ./mvnw -B dependency:go-offline
COPY . .
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN apk upgrade --no-cache && addgroup -S app && adduser -S app -G app
COPY --from=build /app/bootstrap/target/sana3-backend.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
