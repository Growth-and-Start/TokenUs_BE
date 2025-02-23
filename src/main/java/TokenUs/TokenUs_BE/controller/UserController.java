package TokenUs.TokenUs_BE.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.converter.UserConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.jwt.JwtUtil;
import TokenUs.TokenUs_BE.sevice.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 회원가입
    @PostMapping("/singup")
    @Operation(summary = "이메일, 비밀번호, 닉네임을 바탕으로 회원가입", description = "JWT 토큰 반환X, 로그인 API를 사용")
    public ApiResponse<UserResponseDTO.joinResultDTO> join(
            @Validated @RequestBody UserRequestDTO.joinRequestDTO request) {

        User user = userService.joinUser(request);

        UserResponseDTO.joinResultDTO newJoinResult = UserConverter.toJoinResultDTO(user);

        return ApiResponse.onSuccess(newJoinResult);
    }
}
