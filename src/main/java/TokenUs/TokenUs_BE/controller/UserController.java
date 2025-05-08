package TokenUs.TokenUs_BE.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.config.security.CustomUserDetails;
import TokenUs.TokenUs_BE.converter.SubscribeConverter;
import TokenUs.TokenUs_BE.converter.UserConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.dto.UserRequestDTO;
import TokenUs.TokenUs_BE.dto.UserResponseDTO;
import TokenUs.TokenUs_BE.repository.SubscribeRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.service.UserService;
import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;

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

    @PostMapping("/subscribe")
    @Operation(summary = "크리에이터(유저) 구독하기", description = "크리에이터의 id를 넣고 요청하면 현재 로그인한 사용자가 구독하도록 설정")
    public ApiResponse<UserResponseDTO.subscribeResultDTO> subscribe(
            @RequestParam Long targetId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // user id 반환
        Long userId = userDetails.getUser().getId();

        Subscribe subscribe = userService.subscribe(userId, targetId);

        UserResponseDTO.subscribeResultDTO result =
                SubscribeConverter.toResponseDTO(subscribe, true);

        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/unsubscribe")
    @Operation(
            summary = "크리에이터(유저) 구독취소",
            description = "크리에이터의 id를 넣고 요청하면 현재 로그인한 사용자가 구독 취소하도록 설정")
    public ApiResponse<UserResponseDTO.subscribeResultDTO> unsubscribe(
            @RequestParam Long targetId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        // user id 반환
        Long userId = userDetails.getUser().getId();

        Subscribe subscribe = userService.unsubscribe(userId, targetId);

        UserResponseDTO.subscribeResultDTO result =
                SubscribeConverter.toResponseDTO(subscribe, false);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/get_my_info")
    @Operation(summary = "현재 로그인한 사용자의 정보를 반환합니다.", description = "이메일, 지갑주소, id, 프로필 사진url, 닉네임")
    public ApiResponse<UserResponseDTO.userInfoDTO> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        UserResponseDTO.userInfoDTO result = userConverter.toUser(user);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/detail")
    @Operation(
            summary = "userId로 user의 상세정보를 반환합니다.",
            description = "로그인 상태를 가정합니다. 구독 여부를 포함하기 위해")
    public ApiResponse<UserResponseDTO.searchResultDTO> getUserDetail(
            @RequestParam(required = true) Long creatorId) {

        Long currentUserId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            currentUserId = userDetails.getUser().getId();
        }

        User creator = userRepository.findById(creatorId).get();

        Boolean isSubscribed =
                subscribeRepository.existsBySubscriberIdAndTargetId(currentUserId, creator.getId());

        UserResponseDTO.searchResultDTO result =
                userConverter.toSearchResultDTO(creator, isSubscribed);

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/add_wallet")
    @Operation(summary = "지갑 주소 추가", description = "로그인한 사용자의 지갑 주소를 업데이트합니다.")
    public ApiResponse<Void> addWallet(
            @Valid @RequestBody UserRequestDTO.WalletAddressUpdateDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateWalletAddress(userDetails.getUser().getId(), request.getWalletAddress());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/modify_info")
    @Operation(summary = "사용자 정보 수정", description = "프로필 이미지, 닉네임, 비밀번호를 수정할 수 있습니다.")
    public ApiResponse<UserResponseDTO.modifyResultDTO> modifyInfo(
            @Valid @RequestBody UserRequestDTO.modifyInfoDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDTO.modifyResultDTO result =
                userService.modifyUserInfo(userDetails.getUser().getId(), request);
        return ApiResponse.onSuccess(result);
    }
}
