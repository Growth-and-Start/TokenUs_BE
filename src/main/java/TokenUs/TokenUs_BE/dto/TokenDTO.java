package TokenUs.TokenUs_BE.dto;

import lombok.*;

@Data
@Builder
public class TokenDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class tokenDTO {
        private String grantType;
        private String accessToken;
        private Long accessTokenExpiresIn;
        private String refreshToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class refreshTokenRequestDTO {
        private String refreshToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class tokenResponseDTO {
        private String accessToken;
    }
}
