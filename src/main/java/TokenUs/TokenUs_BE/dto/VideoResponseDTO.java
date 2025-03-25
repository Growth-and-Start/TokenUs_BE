package TokenUs.TokenUs_BE.dto;

import java.time.LocalDateTime;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VideoResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class similarityCheckResultDTO {

        @JsonProperty("video_path")
        private String videoPath;

        @JsonProperty("max_similarity")
        private double maxSimilarity;

        @JsonProperty("avg_similarity")
        private double avgSimilarity;

        private String message;

        private boolean passed; // 유사도 검사를 통과했는지 여부

        private String similarVideoId; // ❗ 유사도 검사 실패 시 포함됨 (nullable)
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class uploadResultDTO {

        private Long id;

        @JsonProperty("video_path")
        private String videoPath;

        private LocalDateTime createdAt;
    }
}
