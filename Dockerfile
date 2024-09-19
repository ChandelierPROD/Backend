FROM openjdk:18-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN mvn package

#COPY target/backend-0.0.1-SNAPSHOT.jar backend-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","target/ProdOlymp-0.0.1-SNAPSHOT.jar"]
