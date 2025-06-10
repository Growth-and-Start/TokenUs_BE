# 🪙TokenUs🪙
- 이화여자대학교 컴퓨터공학과 캡스톤디자인과창업프로젝트A,B
- 개발 기간: 2024.09 ~ 2025.06

## Team Info : 8시 스쿼시 연맹
| 안희재 | 서지민 | 김원영 |
| --- | --- | --- |
| @AnyJae | @SeoJimin1234    | @lasagna10 |
| -FE 개발<br>-SmartContract개발 | -BE 개발<br>-ML 개발<br>-SmartContract 개발| -UX/UI 디자인<br>-FE개발<br>-SmartContract 개발 |


## Project Info
 영상을 NFT로 발행하여 영상의 고유 가치를 지키고, 불법 복제를 방지하며, 원저작자의 권리를 보호하고 투자의 기회까지 제공하는 영상 플랫폼.
#### 주요 기능1 - 영상 유사도 검사
사전 학습된 ResNet-50 모델과 Cosine Similarity를 활용한 유사도 검사. 영상의 고유성과 NFT의 가치를 보호하고, 불법 복제 방지.
#### 주요 기능2 - NFT 발행
Ethereum을 기반으로 한 NFT 발행
#### 주요 기능3 - NFT 거래
유저 간 자유로운 NFT 거래. 수익을 기대할 수 있음
### Stacks
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"><br>
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><br>
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><br>
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"><br>
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><br>

## How To Use
#### 1. .env 파일 생성 및 작성

```
# 공통
MYSQL_PASSWORD=
MYSQL_USER=

# MySQL 컨테이너 환경 변수
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=

# Spring Boot 애플리케이션 환경 변수
MYSQL_DRIVER=
MYSQL_URL=

#JWT
JWT_SECRET=

#AWS S3(githubAction User 기준)
AWS_S3_BUCKET=
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
AWS_DEFAULT_REGION=

#Smart Contract(Web3j)
VIDEO_NFT_CONTRACT_ADDRESS=
MARKET_PLACE_CONTRACT_ADDRESS=
PRIVATE_KEY=
SERVER_WALLET_ADDRESS=
RPC_URL
CHAIN_ID=

#Flask 서버
FLASK_URL=
```

#### 2. 디렉터리 이동
```
cd Docker/local/
```

#### 3. docker compose
```
docker-compose up -d
```
