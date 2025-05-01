package TokenUs.TokenUs_BE.controller;

import java.util.List;

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

        // 2. 지갑 주소 확인
        if (user.getWalletAddress() == null || user.getWalletAddress().isEmpty()) {
            throw new IllegalArgumentException("NFT 민팅을 위해서는 지갑 주소가 필요합니다. 먼저 지갑 주소를 등록해주세요.");
        }

        // 3. DTO에 주입
        request.setCreatorAddress(user.getWalletAddress());

        return nftService.mintVideoNFT(request, user);
    }

    @PostMapping("/list")
    @Operation(
            summary = "nft를 마켓 플레이스에 등록",
            description = "단위: wei</br>디자인 및 FE 나오기 전 작업->수정 필요하면 말해주세요")
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

    @GetMapping("/listed")
    @Operation(summary = "판매 등록된 NFT 리스트 반환", description = "")
    public ApiResponse<List<NftResponseDTO.listedNFTInfoDTO>> getListedNFTs() throws Exception {
        return ApiResponse.onSuccess(nftService.getListedNfts());
    }

    @PostMapping("/delist")
    @Operation(summary = "NFT의 판매 등록 취소", description = "NFT의 소유자만 가능")
    public ApiResponse<NftResponseDTO.NFTListResultDTO> delistNFT(
            @RequestBody NftRequestDTO.NftDelistRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails)
            throws Exception {
        return ApiResponse.onSuccess(nftService.delistNFT(request, userDetails.getUser().getId()));
    }

    @PostMapping("/trade")
    @Operation(summary = "NFT 거래", description = "")
    public ApiResponse<NftResponseDTO.NFTPurchaseResultDTO> purchaseNFT(
            @RequestBody NftRequestDTO.NFTPurchaseRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails)
            throws Exception {
        return ApiResponse.onSuccess(
                nftService.purchaseNFT(request, userDetails.getUser().getId()));
    }

    @GetMapping("/trade_history")
    @Operation(
            summary = "videoId로 NFT 거래 내역을 조회합니다.",
            description = "거래 내역은 최신순으로 정렬되며, txHash와 거래 가격을 포함합니다.")
    public ApiResponse<List<NftResponseDTO.NFTTradeHistoryDTO>> getTradeHistory(
            @RequestParam Long videoId) {
        List<NftResponseDTO.NFTTradeHistoryDTO> result =
                nftService.getTradeHistoryByVideoId(videoId);
        return ApiResponse.onSuccess(result);
    }
}
