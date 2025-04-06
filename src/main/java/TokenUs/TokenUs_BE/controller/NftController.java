package TokenUs.TokenUs_BE.controller;

import java.math.BigInteger;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.config.security.CustomUserDetails;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.sevice.NftService;
import TokenUs.TokenUs_BE.sevice.VideoService;
import io.swagger.v3.oas.annotations.Operation;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nft")
public class NftController {

    private final NftService nftService;
    private final VideoService videoService;

    @PostMapping("/mint")
    @Operation(summary = "nft를 발행합니다.", description = "video/save를 완료한 후 반환되는 videoId를 입력해야합니다.")
    public NftResponseDTO.NFTMintResultDTO mintVideoNFT(
            @Validated @RequestBody NftRequestDTO.NFTMintRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails)
            throws Exception {
        // 1. 로그인 유저
        User user = userDetails.getUser();

        // 2. DTO에 주입
        request.setCreatorAddress(user.getWalletAddress());

        return nftService.mintVideoNFT(request, user);
    }

    @PostMapping("/list")
    @Operation(summary = "nft를 마켓 플레이스에 등록", description = "디자인 및 FE 나오기 전 작업->수정 필요하면 말해주세요")
    public ApiResponse<NftResponseDTO.NFTListResultDTO> listNFT(
            @RequestBody NftRequestDTO.listNftRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            NftResponseDTO.NFTListResultDTO result =
                    nftService.listNft(request, userDetails.getUser().getId());
            return ApiResponse.onSuccess(result);
        } catch (Exception e) {
            return ApiResponse.onFailure("LIST_FAIL", e.getMessage(), null);
        }
    }

    @PostMapping("/transfer")
    @Operation(summary = "nft 거래를 위한 API(개발중)")
    public String transferVideoNFT(
            @RequestParam String from, @RequestParam String to, @RequestParam BigInteger tokenId)
            throws Exception {
        return nftService.safeTransferNFT(from, to, tokenId);
    }
}
