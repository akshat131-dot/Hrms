
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app


COPY . .


RUN mvn -B -DskipTests clean package


FROM eclipse-temurin:21-jre
WORKDIR /app


COPY --from=build /app/hrms-main/target/hrms-main-1.0.0-SNAPSHOT.jar app.jar

RUN rm -f app.jar.original || true


EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]