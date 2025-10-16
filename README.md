# 🪙TokenUs🪙

## Team Info
| @AnyJae | @SeoJimin1234    | @lasagna10 |


## Project Info
 A video platform that protects the unique value of videos by issuing them as NFTs, preventing illegal duplication, safeguarding the original creator’s rights, and providing investment opportunities.
#### [Key Feature 1 - Video Similarity Check]
Uses a pre-trained ResNet-50 model and cosine similarity to analyze and detect video similarity. This ensures the originality of videos, preserves NFT value, and prevents unauthorized copies.
#### [Key Feature 2 - NFT Minting]
Enables video NFT minting based on the Ethereum blockchain.
#### [Key Feature 3 - NFT Trading]
Allows users to freely trade NFTs, offering opportunities for profit.
#### [Automated Profit Distribution]
Implements smart contract–based automated profit sharing, ensuring transparent and fair revenue distribution among creators, investors, and rights holders.

### Stacks
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white"><br>
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"><br>
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><br>
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"><br>
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"><br>

## How To Use
#### 1. Create and Configure the .env File

```
# Common
MYSQL_PASSWORD=
MYSQL_USER=

# MySQL Container Environment Variables
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=

# Spring Boot Application Environment Variables
MYSQL_DRIVER=
MYSQL_URL=

#JWT
JWT_SECRET=

# AWS S3
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

#Flask
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
