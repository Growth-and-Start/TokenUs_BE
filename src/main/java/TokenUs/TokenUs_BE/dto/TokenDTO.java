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
        private String grantType; // 토큰 타입 (Bearer)
        private String accessToken; // 액세스 토큰
        private Long accessTokenExpiresIn; // 액세스 토큰 만료 시간 (Unix Timestamp)
        private String refreshToken; // 리프레시 토큰
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
