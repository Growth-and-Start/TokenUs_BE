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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTInfoDTO {
        private BigInteger tokenId;
        private BigInteger currentPrice;
        private Long videoId;
        private Long userId;
        private Boolean isList;
        private BigInteger mintQuantity;
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
}
