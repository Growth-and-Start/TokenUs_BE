package TokenUs.TokenUs_BE.controller;

import java.math.BigInteger;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

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
            @Validated @RequestBody NftRequestDTO.NFTMintRequestDTO request) throws Exception {
        // 1. 로그인 유저
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 2. DTO에 주입
        request.setCreatorAddress(user.getWalletAddress());

        return nftService.mintVideoNFT(request);
    }

    @PostMapping("/transfer")
    @Operation(summary = "nft 거래를 위한 API(개발중)")
    public String transferVideoNFT(
            @RequestParam String from, @RequestParam String to, @RequestParam BigInteger tokenId)
            throws Exception {
        return nftService.safeTransferNFT(from, to, tokenId);
    }
}
