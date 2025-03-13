package TokenUs.TokenUs_BE.controller;

import java.math.BigInteger;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.sevice.NftService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/nft")
public class NftController {

    private final NftService nftService;

    public NftController(NftService nftService) {
        this.nftService = nftService;
    }

    @PostMapping("/mint")
    @Operation(
            summary = "nft를 발행합니다.",
            description = "metadataUri, totalSupply, nftName, nftSymbol을 RequestBody에 넣어서 요청합니다.")
    public String mintVideoNFT(@Validated @RequestBody NftRequestDTO.NFTMintRequestDTO request)
            throws Exception {

        String metadataUri = request.getMetadataUri();
        BigInteger totalSupply = request.getTotalSupply();
        String nftName = request.getNftName();
        String nftSymbol = request.getNftSymbol();

        return nftService.mintVideoNFT(metadataUri, totalSupply, nftName, nftSymbol);
    }

    @PostMapping("/transfer")
    @Operation(summary = "nft 거래를 위한 API(개발중)")
    public String transferVideoNFT(
            @RequestParam String from, @RequestParam String to, @RequestParam BigInteger tokenId)
            throws Exception {
        return nftService.safeTransferNFT(from, to, tokenId);
    }
}
