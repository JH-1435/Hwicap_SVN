# docker hub 사이트에서 이미지 다운 : 누군가 업로드한 가상의 컴퓨터를 다운 받는것.
# Docker Build : Dockerfile 실행하여 이미지를 업로드(or update) 하는 것.

# docker hub 명렁어
# FROM : docker pull 즉 도커허브 사이트에서 가져오기
#  : docker push 즉 도커허브 사이트에 올리기
# RUN : 도커 빌드 단계에서 실행 O, 빌드 후 이미지 OS 의 터미널 명령어 사용
# CMD : 도커 빌드 단계에서 실행 X, 해당 도커 프로그램을 실행 했을때 사용
# => CMD는 보통 마지막에 실행하는 명령어로 쓰이며, CMD 는 Dockerfile 내에서 한번 밖에 못씀
# COPY "복사할 내 파일" "도커 폴더/붙여넣기 할 파일명": 내 컴퓨터에 있는 파일/폴더를 도커 안으로 복사하는 명령어
# WORKDIR 폴더명 : 작업 디렉토리를 설정하는 것, 디렉토리가 없으면 자동으로 mkdir처럼 생성 해줌


# 1. 운영체제(OS) 및 프로그램 설치(하나하나 각각 입력하는 방식)
# FROM ubuntu:22.04

# RUN sudo apt install nodejs
# RUN sudo npm install -g yarn



# 1. 운영체지 및 프로그램 설치(이미 리눅스, node, npm, yarn 까지 모두 깔려있는 이미지 다운로드 하는 방식)
# node : 18 을 사용하여  Node.js 18 버전 설치
FROM node:18

# 2. 내 컴퓨터에 있는 폴더나 파일을 도커 컴퓨터 안으로 복사하기
# RUN mkdir hwicapDocker => 아래에서 COPY할 때, 어차피 자동으로 만들어주므로 굳이 필요 없음.
# RUN cd hwicapDocker
# COPY ./index.js /hwicapDocker/index.js

# 2. vim 설치 (컨테이너 편집기) \ 한글 깨짐 방지용 설정 추가
# ==> 실시간으로 index.js 등 편집 가능하게 해줌
RUN apt-get update && apt-get install -y vim && \
    echo "set encoding=utf-8\nset fileencodings=utf-8,euc-kr\nset termencoding=utf-8" > ~/.vimrc


# 3. 내 컴퓨터에 있는 폴더나 파일을 도커 컴퓨터 안으로 복사하기 (WORKDIR 이용)
# COPY는 /hwicapDocker/index.js에 자동으로 들어가게됨.
# 작업 디렉터리 설정
# Dockerfile은 각각의 컨테이너 내에서 /app 디렉토리 내에서 작업하지만, 서로 다른 이미지로 빌드되고 독립적으로 실행
WORKDIR /hwicapDocker

# 4. 만약 사용자 각각 node 모듈즈의 버전이 달라도 Docker 컴퓨터 안에서 node 18 버전으로 설치하기 때문에 상관 X
# ==> 즉, node 모듈즈의 버전이 각각 다른 사용자들은 Docker 컴퓨터의 node 18 버전으로 통일됨.
# ===> .dockerignore : Dockerfile 에서 COPY 제외 목록
# package.json과 yarn.lock 파일을 먼저 복사하고 의존성 설치
# => package.json yarn.lock이 변경되지 않으면 COPY 도 캐시(임시저장공간)타고, RUN yarn install도 캐시타서 건너띔
# => 즉 의존성 캐시(임시저장공간) 최적화, 빌드 효율성 증가(캐시 구조 최적화)
COPY package.json yarn.lock ./
RUN yarn install

# 5. 나머지 파일 복사
# => 파일 변경 시 캐시(임시저장공간) 깨지면서 package.json yarn.lock 을 제외한 모든 파일 다시 빌드 함.
COPY . .

# 6.3000 port 노출
EXPOSE 3000

# 7. 도커안에서 애플리케이션 시작
# CMD ["node", "index.js"]
CMD ["yarn", "start:dev"]



