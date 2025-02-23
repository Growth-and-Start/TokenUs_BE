package TokenUs.TokenUs_BE.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDTO {
    private String grantType; // 토큰 타입 (Bearer)
    private String accessToken; // 액세스 토큰
    private Long accessTokenExpiresIn; // 액세스 토큰 만료 시간 (Unix Timestamp)
    private String refreshToken; // 리프레시 토큰
}
