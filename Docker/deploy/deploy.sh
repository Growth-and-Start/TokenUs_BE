# 이 파일 내용은 모두 임시입니다.

# ECR에서 최신 이미지 pull
echo "ECR에 있는 이미지 불러오기"
if ! docker pull 061039770972.dkr.ecr.ap-northeast-2.amazonaws.com/tokenus-docker:latest; then
    echo "이미지 불러오기에 실패했습니다."
    exit 1
fi

# Docker compose down으로 기존 컨테이너 중지 및 삭제
echo "Docker compose down 실행"
docker compose down

# Docker compose up 실행
echo "Docker compose up 실행"
if ! docker compose up -d; then
    echo "컨테이너 실행에 실패했습니다"
    exit 1
fi

# dangling 이미지 삭제
echo "dangling 이미지 삭제"
docker image prune -f

echo "멈춘 container 삭제"
docker container prune -f

for i in {1..10}; do
    if [ "$i" -eq 10 ]; then
       echo "Health check failed"
       docker compose down
       exit 1
    fi

    if curl "http://localhost:8080/health"; then
        echo "컨테이너가 정상적으로 실행되었습니다..."
        break
    fi

    echo "spring boot application health check 중..."
    sleep 15
done

echo "모든 작업이 완료되었습니다."