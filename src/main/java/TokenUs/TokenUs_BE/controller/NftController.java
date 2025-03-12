package TokenUs.TokenUs_BE.controller;

import java.math.BigInteger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TokenUs.TokenUs_BE.sevice.NftService;

@RestController
@RequestMapping("/nft")
public class NftController {

    private final NftService nftService;

    public NftController(NftService nftService) {
        this.nftService = nftService;
    }

    @PostMapping("/mint")
    public String mintVideoNFT(
            @RequestParam String metadataURI,
            @RequestParam BigInteger totalSupply,
            @RequestParam String NFTname,
            @RequestParam String NFTsymbol)
            throws Exception {
        return nftService.mintVideoNFT(metadataURI, totalSupply, NFTname, NFTsymbol);
    }

    @PostMapping("/transfer")
    public String transferVideoNFT(
            @RequestParam String from, @RequestParam String to, @RequestParam BigInteger tokenId)
            throws Exception {
        return nftService.safeTransferNFT(from, to, tokenId);
    }
}
