# 1단계: 빌드 단계
FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app

# Gradle 빌드에 필요한 소스와 build.gradle, settings.gradle, Gradle Wrapper를 복사
COPY build.gradle settings.gradle gradlew* ./
COPY gradle ./gradle
COPY src ./src

# Gradle Wrapper를 사용하여 애플리케이션을 빌드
RUN chmod +x gradlew
RUN ./gradlew build -x test
RUN ls -al /app/build/libs # 빌드된 jar 파일 목록 출력

# 2단계: 실행 단계
FROM openjdk:17.0.2-slim
WORKDIR /app

# curl 설치
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 빌드 단계에서 생성된 JAR 파일을 복사
COPY --from=build /app/build/libs/MeetOn-0.0.1-SNAPSHOT.jar /app/app.jar

# Elastic APM 에이전트 다운로드
RUN curl -L -o elastic-apm-agent.jar https://search.maven.org/remotecontent?filepath=co/elastic/apm/elastic-apm-agent/1.50.0/elastic-apm-agent-1.50.0.jar

ARG RDS_PASSWORD
ARG RDS_URL
ARG RDS_USERNAME
ARG KAFKA_SERVER_URL
ARG SERVER_URL
ARG JWT_SECRET_KEY
ARG APM_SERVICE_NAME
ARG APM_SECRET_TOKEN
ARG APM_SERVER_URL
ARG APM_ENVIRONMENT

# 환경 변수를 ENV로 설정
ENV RDS_PASSWORD=${RDS_PASSWORD}
ENV RDS_URL=${RDS_URL}
ENV RDS_USERNAME=${RDS_USERNAME}
ENV KAFKA_SERVER_URL=${KAFKA_SERVER_URL}
ENV SERVER_URL=${SERVER_URL}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV APM_SERVICE_NAME=${APM_SERVICE_NAME}
ENV APM_SECRET_TOKEN=${APM_SECRET_TOKEN}
ENV APM_SERVER_URL=${APM_SERVER_URL}
ENV APM_ENVIRONMENT=${APM_ENVIRONMENT}

# JVM 플래그 및 애플리케이션 실행
CMD ["sh", "-c", "java -javaagent:/app/elastic-apm-agent.jar \
    -Delastic.apm.service_name=${APM_SERVICE_NAME} \
    -Delastic.apm.secret_token=${APM_SECRET_TOKEN} \
    -Delastic.apm.server_url=${APM_SERVER_URL} \
    -Delastic.apm.environment=${APM_ENVIRONMENT} \
    -Delastic.apm.application_packages=semicolon.MeetOn \
    -Dspring.profiles.active=prod \
    -jar /app/app.jar"]

