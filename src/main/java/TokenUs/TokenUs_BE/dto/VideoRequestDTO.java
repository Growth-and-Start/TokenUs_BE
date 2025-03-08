package TokenUs.TokenUs_BE.dto;

import lombok.*;

@Getter
@Setter
public class VideoRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class uploadRequestDTO {

        private String filename;
    }
}
