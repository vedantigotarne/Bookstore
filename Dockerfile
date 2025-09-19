FROM eclipse-temurin:17-jdk as build
WORKDIR /workspace/app

# Copy maven executable and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make the maven wrapper executable
RUN chmod +x ./mvnw

# Build all dependencies (this will be cached unless the POM changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Production stage
FROM eclipse-temurin:17-jre
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Set default environment variables
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Use ENTRYPOINT to configure the container startup command
ENTRYPOINT ["java", "-cp", "app:app/lib/*", \
           "-Dserver.port=${PORT}", \
           "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
           "com.example.bookstore.BookstoreApplication"]
