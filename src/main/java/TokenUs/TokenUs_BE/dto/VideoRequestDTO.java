package TokenUs.TokenUs_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VideoRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class videoDetailRequestDTO {

        // video 정보
        private String videoTitle;
        private String videoDetail;
        private String videoUrl;
        private Boolean isOpen;

        private Boolean isOpen;

        @Builder.Default private String thumbnailUrl = "https://example.com/default-thumbnail.jpg";
    }
}
