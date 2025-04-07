package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NftResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTMintResultDTO {

        private String transactionHash;
        private List<NFTInfoDTO> mintedNFTs;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTInfoDTO {
        private BigInteger tokenId;
        private BigInteger currentPrice;
        private Long videoId;
        private Long userId;
        private Boolean isListed;
        private BigInteger mintQuantity;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class listedNFTInfoDTO {
        private BigInteger tokenId;
        private BigInteger currentPrice;
        private Long videoId;
        private Boolean isListed;
        private Long creatorId;
        private String sellerWallet;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTListResultDTO {
        private String transactionHash;
        private BigInteger tokenId;
        private BigInteger price;
        private String sellerAddress;
        private Boolean isListed;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTPurchaseResultDTO {
        private String transactionHash;
        private BigInteger tokenId;
        private String buyerAddress;
        private BigInteger tradePrice;
    }
}
