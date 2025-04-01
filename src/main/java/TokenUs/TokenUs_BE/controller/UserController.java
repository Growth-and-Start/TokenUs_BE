package TokenUs.TokenUs_BE.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.config.security.CustomUserDetails;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.sevice.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(
            summary = "크리에이터 검색",
            description = "검색어와 일치하는 크리에이터 리스트 반환, 현재 로그인한 사용자가 구독했는지 여부 반환")
    public ApiResponse<List<UserResponseDTO.searchResultDTO>> searchUsers(
            @RequestParam String searchFor, HttpServletRequest request) {

        Long currentUserId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            currentUserId = userDetails.getUser().getId();
        }

        List<UserResponseDTO.searchResultDTO> result =
                userService.searchUsers(searchFor, currentUserId);
        return ApiResponse.onSuccess(result);
    }
}
