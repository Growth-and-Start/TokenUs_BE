package TokenUs.TokenUs_BE.dto;

import jakarta.validation.constraints.NotBlank;

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

        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private String detail;

        private String fileUrl;

        @Builder.Default private String thumbnailUrl = "https://example.com/default-thumbnail.jpg";
    }
}
