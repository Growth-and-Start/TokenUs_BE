package TokenUs.TokenUs_BE.dto;

import lombok.*;

@Getter
@Setter
public class VideoResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class uploadResponseDTO {
        private String message;
        private String s3Url; // S3 저장 URL
        private String error;
        private Double similarity;
    }
}
