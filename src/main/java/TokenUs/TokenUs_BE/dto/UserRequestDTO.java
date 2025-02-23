package TokenUs.TokenUs_BE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class joinRequestDTO {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname;

        String walletAddress;
    }
}
