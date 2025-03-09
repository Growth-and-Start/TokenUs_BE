package TokenUs.TokenUs_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class VideoResponseDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class similarityCheckResultDTO {
        private String videoPath;
        private double maxSimilarity;
        private double avgSimilarity;
        private String message;
        private boolean passed; // 유사도 검사를 통과했는지 여부
    }
}
