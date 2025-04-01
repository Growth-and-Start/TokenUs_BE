package TokenUs.TokenUs_BE.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class joinResultDTO {
        Long id;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class loginResultDTO {

        private String accessToken;

        private String refreshToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class searchResultDTO {

        private Long id;
        private String nickName;
        private String profileImageUrl;
        private String email;
        private boolean isSubscribed;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class subscribeResultDTO {

        private Long subscriberId;
        private Long targetId;
        private boolean isSubscribed;
    }

}
