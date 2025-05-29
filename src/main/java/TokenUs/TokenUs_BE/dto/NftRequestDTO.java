package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;
import java.util.List;

import lombok.*;

public class NftRequestDTO {

    // emit VideoNFTMinted(videoId, creatorAddress, totalSupply, nftName, nftSymbol, price);
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTMintRequestDTO {

        private BigInteger totalSupply;
        private String nftName;
        private String nftSymbol;
        private BigInteger price;
        private BigInteger videoId;

        // 클라이언트가 보내지 않지만 백엔드에서 주입될 값들
        private String creatorAddress;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTMintRequestGetDTO {
        private BigInteger totalSupply;
        private String nftName;
        private String nftSymbol;
        private BigInteger price;
        private BigInteger videoId;
        private String creatorAddress;
        private Long creatorId;
        private String txHash;
        private List<BigInteger> tokenIdList;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class listNftRequestDTO {
        private BigInteger tokenId;
        private BigInteger price; // wei 단위
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class listNftResultGetDTO {
        private BigInteger tokenId;
        private BigInteger price; // wei 단위
        private String txHash;
        private String creatorAddress;
        private String creatorId; // 프론트에서 넣어주세요
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NftDelistRequestDTO {
        private BigInteger tokenId;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTPurchaseRequestDTO {
        private BigInteger tokenId;
    }
}
