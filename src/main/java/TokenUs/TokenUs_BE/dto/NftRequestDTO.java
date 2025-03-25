package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;

import lombok.*;

public class NftRequestDTO {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTMintRequestDTO {
        // nft 정보
        @Builder.Default
        private String metadataUri =
                "https://tokenus-storage.s3.ap-northeast-2.amazonaws.com/profile/second.png"; // ✅

        // 기본값
        // 설정

        private BigInteger totalSupply;
        private String nftName;
        private String nftSymbol;
        private BigInteger price;
        private BigInteger videoId;

        // 클라이언트가 보내지 않지만 백엔드에서 주입될 값들
        private String creatorAddress;
    }
}
