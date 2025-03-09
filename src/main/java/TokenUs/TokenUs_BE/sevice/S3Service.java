package TokenUs.TokenUs_BE.sevice;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /** Presigned URL 생성 메서드 */
    public String generatePresignedUrl(String originalFileName, String contentType) {

        String finalFileName = originalFileName;

        // 기존 파일이 존재하면 숫자 붙이기
        int count = 1;
        while (isFileExists(finalFileName)) {
            String nameWithoutExtension =
                    originalFileName.substring(0, originalFileName.lastIndexOf("."));
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            finalFileName = nameWithoutExtension + "_" + count + extension;
            count++;
        }

        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(finalFileName)
                        .contentType(contentType)
                        .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10)) // 10분간 유효
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public boolean isFileExists(String fileName) {
        try {
            HeadObjectRequest headRequest =
                    HeadObjectRequest.builder().bucket(bucketName).key(fileName).build();
            s3Client.headObject(headRequest); // 파일이 존재하는지 확인
            return true; // 파일 존재
        } catch (NoSuchKeyException e) {
            return false; // 파일 없음
        }
    }
}
