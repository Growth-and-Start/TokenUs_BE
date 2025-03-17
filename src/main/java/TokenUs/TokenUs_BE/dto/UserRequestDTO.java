package TokenUs.TokenUs_BE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.*;

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

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String name;

        String profileUrl;

        String walletAddress;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class loginRequestDTO {

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class emailCheckDTO {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 아닙니다.")
        String email;
    }
}
