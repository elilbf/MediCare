FROM maven:3.9.6-amazoncorretto-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY user-service/pom.xml user-service/
COPY scheduling-service/pom.xml scheduling-service/
COPY notification-service/pom.xml notification-service/

RUN mvn dependency:go-offline -B

COPY user-service/src user-service/src
COPY scheduling-service/src scheduling-service/src
COPY notification-service/src notification-service/src

ARG SERVICE_NAME
ENV SERVICE_NAME=${SERVICE_NAME}

RUN mvn clean package -pl ${SERVICE_NAME} -am -DskipTests

FROM amazoncorretto:21-alpine AS runtime

WORKDIR /app

ARG SERVICE_NAME
ENV SERVICE_NAME=${SERVICE_NAME}

COPY --from=builder /app/${SERVICE_NAME}/target/${SERVICE_NAME}-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
