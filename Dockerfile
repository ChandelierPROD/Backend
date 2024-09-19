FROM openjdk:18-slim

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the rest of the project files
COPY . .

# Build the application
RUN mvn package

# Copy the jar file from target
COPY target/ProdOlymp-0.0.1-SNAPSHOT.jar /app/ProdOlymp-0.0.1-SNAPSHOT.jar

# Set the entry point
ENTRYPOINT ["java","-jar","/app/ProdOlymp-0.0.1-SNAPSHOT.jar"]
