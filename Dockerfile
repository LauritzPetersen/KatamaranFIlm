# Trin 1: Byg programmet med Maven og Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Her bygger vi din .jar fil præcis som IntelliJ gør det
RUN mvn clean package -DskipTests

# Trin 2: Kør det færdige program i en letvægts container
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Vi kopierer den færdige fil fra Trin 1
COPY --from=build /app/target/KatamaranFIlm-0.0.1-SNAPSHOT.jar app.jar
# Fortæller Render at vores app lytter på port 8080
EXPOSE 8080
# Kommandoen der starter programmet
ENTRYPOINT ["java","-jar","app.jar"]