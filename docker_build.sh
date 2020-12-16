#!/bin/bash
echo "> Git Pull"

#git pull

CURRENT_PID=$(pgrep -f petmily)

git checkout main

git pull

./gradlew build

echo "> 현재 구동중인 애플리케이션 pid 확인"

echo "$CURRENT_PID"

if [ -z $CURRENT_PID ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -2 $CURRENT_PID"
    kill -9 $CURRENT_PID
    sleep 1
fi
echo "> 도커 컨테이너 종료"
docker stop petmily
echo "> 도커 컨테이너 삭제"
docker rm petmily
echo "> 도커 이미지 삭제"
docker rmi petmily-spring-boot-docker:latest
echo "> 도커 이미지 빌드"
docker build --build-arg JAR_FILE=build/libs/*.jar --build-arg ENVIRONMENT=test -t petmily-spring-boot-docker .
echo "> 도커 컨테이너 실행"
docker run -i -t --name petmily -p 8086:8086 petmily-spring-boot-docker
echo "> 새 어플리케이션 배포"

