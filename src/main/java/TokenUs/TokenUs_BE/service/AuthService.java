package TokenUs.TokenUs_BE.service;

import java.util.Collections;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.code.status.SuccessStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.handler.GeneralHandler;
import TokenUs.TokenUs_BE.converter.UserConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.dto.TokenDTO;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.jwt.JwtUtil;
import TokenUs.TokenUs_BE.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final JwtUtil jwtUtil;

    // 회원가입
    public User joinUser(UserRequestDTO.joinRequestDTO request) {

        // 해당 email의 유저가 이미 존재
        if (userRepository.findByEmail((request.getEmail())).isPresent()) {
            throw new GeneralHandler(ErrorStatus.USER_ALREADY_EXIST);
        }

        User newUser = userConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.save(newUser);
    }

    // 로그인
    @Transactional
    public UserResponseDTO.loginResultDTO loginUser(UserRequestDTO.loginRequestDTO request) {

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GeneralHandler(ErrorStatus.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String accessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        Collections.singleton(user.getRole()),
                        1000 * 60 * 60 * 24L, // 30분 만료
                        "access");
        String refreshToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        Collections.singleton(user.getRole()),
                        1000 * 60 * 60 * 24 * 7L, // 7일 만료
                        "refresh");

        // Refresh Token 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return userConverter.toLoginResultDTO(accessToken, refreshToken);
    }

    @Transactional
    public ApiResponse<TokenDTO.tokenResponseDTO> refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.USER_NOT_FOUND));

        // 저장된 Refresh Token과 요청된 Refresh Token이 일치하는지 확인
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new GeneralHandler(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 새로운 Access Token 발급
        String newAccessToken =
                jwtUtil.generateToken(
                        user.getEmail(),
                        Collections.singleton(user.getRole()),
                        1000 * 60 * 30L,
                        "access");

        // ApiResponse 형식에 맞게 반환
        return ApiResponse.of(SuccessStatus._OK, new TokenDTO.tokenResponseDTO(newAccessToken));
    }

    @Transactional
    public void logout(String refreshToken) {
        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new GeneralHandler(ErrorStatus.USER_NOT_FOUND));

        user.updateRefreshToken(null);
        userRepository.save(user);
    }

    public boolean check_email_duplication(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
