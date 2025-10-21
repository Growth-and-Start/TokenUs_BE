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
        private String nftName;
        private String nftSymbol;
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
        private Long id;
        private BigInteger tokenId;
        private String nftName;
        private String nftSymbol;
        private BigInteger currentPrice;
        private BigInteger mintPrice;
        private BigInteger floorPrice;
        private Long videoId;
        private Boolean isPrimary;
        private Boolean isListed;
        private Long creatorId;
        private String sellerWallet;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTListResultDTO {
        private Long id;
        private String transactionHash;
        private BigInteger tokenId;
        private String nftName;
        private String nftSymbol;
        private BigInteger currentPrice;
        private BigInteger mintPrice;
        private String sellerAddress;
        private Boolean isListed;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTPurchaseResultDTO {
        private String purchaseTxHash;
        private String transferTxHash;
        private BigInteger tokenId;
        private String nftName;
        private String nftSymbol;
        private String buyerAddress;
        private BigInteger tradePrice;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTPurchaseResultGetDTO {
        private String txHash;
        private BigInteger tokenId;
        private String nftName;
        private String nftSymbol;
        private String buyerAddress;
        private BigInteger tradePrice;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NFTTradeHistoryDTO {
        private String txHash;
        private BigInteger tradePrice;
        private String createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoInterestDTO {
        private Long videoId;
        private String message;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyNftDTO {

        private BigInteger tokenId;
        private Boolean isListed;
        private String nftName;
        private String nftSymbol;
        private Long videoId;
        private String videoThumbnailUrl;
        private String videoTitle;
        private String videoUrl;
        private Long creatorId;
        private Boolean isPrimary;
        private String creatorNickname;
        private String creatorProfileUrl;
        private BigInteger purchasedPrice;
        private BigInteger floorPrice;
        private BigInteger currentPrice;
        private BigInteger mintPrice;
    }
}
