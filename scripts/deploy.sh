#!/bin/bash
cd /home/ubuntu/app

DOCKER_APP_NAME=spring

# 실행중인 blue가 있는지
EXIST_BLUE=$(docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep running)

# green이 실행중이면 blue up
if [ -z "$EXIST_BLUE" ]; then
	echo "blue up"
	docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build

	sleep 30

	docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml down
	docker image prune -af # 사용하지 않는 이미지 삭제

# blue가 실행중이면 green up
else
	echo "green up"
	docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build

	sleep 30

	docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml down
	docker image prune -af
fi


#echo "> 현재 실행 중인 Docker 컨테이너 pid 확인dlqldldl" >> /home/ubuntu/deploy.log
#CURRENT_PID=$(sudo docker container ls -q)
#
#if [ -z $CURRENT_PID ]
#then
#  echo "> 현재 구동중인 Docker 컨테이너가 없으므로 종료하지 않습니다." >> /home/ubuntu/deploy.log
#else
#  echo "> sudo docker stop $CURRENT_PID"   # 현재 구동중인 Docker 컨테이너가 있다면 모두 중지
#  sudo docker stop $CURRENT_PID
#  sleep 5
#fi
#
#cd /home/ubuntu/app
#sudo docker build -t image:v1.0 .
#sudo docker run -d -p 8080:8080 image:v1.0
