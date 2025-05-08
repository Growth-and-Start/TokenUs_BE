package TokenUs.TokenUs_BE.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.service.NftService;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final NftService nftService;
    private final UserRepository userRepository;

    @GetMapping("/my_nft")
    public ApiResponse<List<NftResponseDTO.NFTListResultDTO>> getMyNFTs(
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Long userId =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."))
                        .getId();
        List<NftResponseDTO.NFTListResultDTO> myNFTs = nftService.getMyNFTs(userId);
        return ApiResponse.onSuccess(myNFTs);
    }
}
