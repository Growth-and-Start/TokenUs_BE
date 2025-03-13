package TokenUs.TokenUs_BE.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {
    //    private final JwtUtil jwtUtil;
    //    private final AuthService userService;
    //
    //    // 회원가입
    //    @PostMapping("/signup")
    //    @Operation(summary = "이메일, 비밀번호, 닉네임을 바탕으로 회원가입", description = "JWT 토큰 반환X, 로그인 API를 사용")
    //    public ApiResponse<UserResponseDTO.joinResultDTO> join(
    //            @Validated @RequestBody UserRequestDTO.joinRequestDTO request) {
    //
    //        User user = userService.joinUser(request);
    //
    //        UserResponseDTO.joinResultDTO newJoinResult = UserConverter.toJoinResultDTO(user);
    //
    //        return ApiResponse.onSuccess(newJoinResult);
    //    }
    //
    //    @PostMapping("/login")
    //    @Operation(summary = "이메일, 비밀번호를 바탕으로 로그인", description = "JWT 액세스 토큰과 리프레시 토큰 반환")
    //    public ApiResponse<UserResponseDTO.loginResultDTO> login(
    //            @Validated @RequestBody UserRequestDTO.loginRequestDTO request) {
    //
    //        UserResponseDTO.loginResultDTO response = userService.loginUser(request);
    //
    //        return ApiResponse.onSuccess(response);
    //    }
    //
    //    @PostMapping("/logout")
    //    @Operation(summary = "로그아웃 - 리프레시 토큰 삭제")
    //    public ApiResponse<Void> logout(@RequestBody TokenDTO.refreshTokenRequestDTO request) {
    //        userService.logout(request.getRefreshToken());
    //        return ApiResponse.onSuccess(null);
    //    }
}
