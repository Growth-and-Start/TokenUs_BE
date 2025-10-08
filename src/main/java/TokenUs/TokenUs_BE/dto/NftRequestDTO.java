package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;
import java.util.List;

import lombok.*;

public class NftRequestDTO {

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
        private BigInteger price;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class listNftResultGetDTO {
        private BigInteger tokenId;
        private BigInteger price;
        private String txHash;
        private String creatorAddress;
        private String creatorId;
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

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTPurchaseResultGetDTO {
        private BigInteger tokenId;
        private String txHash;
        private String buyerAddress;
        private BigInteger tradePrice;
    }
}
