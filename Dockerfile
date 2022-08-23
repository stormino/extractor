FROM maven:3.8.6-jdk-11 as builder
WORKDIR /opt/app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre-alpine as runner
WORKDIR /opt/app
EXPOSE 28888
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]