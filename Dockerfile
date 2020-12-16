FROM openjdk:11-jre-slim

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ARG PRIVATE_KEY_FILE=./src/main/resources/token_key.pem
COPY ${PRIVATE_KEY_FILE} /src/main/resources/token_key.pem

# ENVIRONMENT라는 이름의 argument를 받을 수 있도록 설정
ARG ENVIRONMENT
# argument로 받은 ENVIRONMENT 값을 SPRING_PROFILES_ACTIVE에 적용
ENV SPRING_PROFILES_ACTIVE=${ENVIRONMENT}
ENTRYPOINT ["java","-jar","/app.jar"]