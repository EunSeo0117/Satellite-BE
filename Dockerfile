# Build Stage
FROM gradle:8.6-jdk21 AS builder
WORKDIR /workspace/app

# 로컬 be 프로젝트 전체 복사
COPY . .

# 그 안에서 gradle build 수행 → jar 생성됨: /workspace/app/build/libs/*.jar
RUN gradle clean build -x test

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /app/uploads

# 빌드한 jar 복사 (builder의 WORKDIR 기준)
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

VOLUME ["/app/uploads"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
