# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml trước để tận dụng cache layer khi dependencies không đổi
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy file jar đã build từ stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]