# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first so dependencies can be cached
COPY pom.xml ./

RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user
RUN groupadd --system stocktracker \
    && useradd --system \
       --gid stocktracker \
       --create-home \
       stocktracker

COPY --from=build /app/target/*.jar ./app.jar

RUN chown stocktracker:stocktracker ./app.jar

USER stocktracker

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]