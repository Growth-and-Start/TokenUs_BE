package TokenUs.TokenUs_BE.converter;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.User;
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

    public static User toUser(UserRequestDTO.joinRequestDTO request) {

        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .walletAddress(request.getWalletAddress())
                .nickname(request.getNickname())
                .build();
    }

    public static UserResponseDTO.loginResultDTO toLoginResultDTO(
            String accessToken, String refreshToken) {
        return new UserResponseDTO.loginResultDTO(accessToken, refreshToken);
    }
}
