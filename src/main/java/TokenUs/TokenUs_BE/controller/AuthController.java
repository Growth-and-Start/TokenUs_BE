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
import TokenUs.TokenUs_BE.dto.TokenDTO;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.jwt.JwtUtil;
import TokenUs.TokenUs_BE.sevice.AuthService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(
            summary = "이메일, 비밀번호, 닉네임, 이름을 바탕으로 회원가입 - profileUrl, walletAddress는 optional",
            description = "JWT 토큰 반환X, 로그인 API를 사용")
    public ApiResponse<UserResponseDTO.joinResultDTO> join(
            @Validated @RequestBody UserRequestDTO.joinRequestDTO request) {

        User user = authService.joinUser(request);

        UserResponseDTO.joinResultDTO newJoinResult = UserConverter.toJoinResultDTO(user);

        return ApiResponse.onSuccess(newJoinResult);
    }

    @PostMapping("/login")
    @Operation(summary = "이메일, 비밀번호를 바탕으로 로그인", description = "JWT 액세스 토큰과 리프레시 토큰 반환")
    public ApiResponse<UserResponseDTO.loginResultDTO> login(
            @Validated @RequestBody UserRequestDTO.loginRequestDTO request) {

        UserResponseDTO.loginResultDTO response = authService.loginUser(request);

        return ApiResponse.onSuccess(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "리프레시 토큰을 이용해 새로운 액세스 토큰 발급")
    public ApiResponse<TokenDTO.tokenResponseDTO> refreshAccessToken(
            @RequestBody TokenDTO.refreshTokenRequestDTO request) {

        return authService.refreshAccessToken(request.getRefreshToken());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 - 리프레시 토큰 삭제")
    public ApiResponse<Void> logout(@RequestBody TokenDTO.refreshTokenRequestDTO request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.onSuccess(null);
    }
}
