package TokenUs.TokenUs_BE.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.enums.Status;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;

@Component
@RequiredArgsConstructor
public class UserConverter {

    public static UserResponseDTO.joinResultDTO toJoinResultDTO(User user) {
        return UserResponseDTO.joinResultDTO
                .builder()
                .id(user.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 회원가입시 사용
    public static User toUser(UserRequestDTO.joinRequestDTO request) {

        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .walletAddress(request.getWalletAddress())
                .nickname(request.getNickname())
                .name(request.getName())
                .status(Status.ACTIVE)
                .profile_image(request.getProfileUrl())
                .build();
    }

    public static UserResponseDTO.userInfoDTO toUser(User user) {
        return UserResponseDTO.userInfoDTO
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfile_image())
                .walletAddress(user.getWalletAddress())
                .build();
    }

    public static UserResponseDTO.loginResultDTO toLoginResultDTO(
            String accessToken, String refreshToken) {
        return new UserResponseDTO.loginResultDTO(accessToken, refreshToken);
    }

    public static UserResponseDTO.searchResultDTO toSearchResultDTO(
            User user, boolean isSubscribed) {
        return UserResponseDTO.searchResultDTO
                .builder()
                .id(user.getId())
                .email(user.getEmail())
                .profileImageUrl(user.getProfile_image())
                .nickName(user.getNickname())
                .isSubscribed(isSubscribed)
                .build();
    }
}
