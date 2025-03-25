package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NftRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NFTMintRequestDTO {
        @NotBlank(message = "metadataURI는 필수 입력값입니다.")
        String metadataUri;

        @NotNull(message = "총 발행량은 필수 입력값입니다.") BigInteger totalSupply;

        @NotBlank(message = "nft이름은 필수 입력값입니다.")
        String nftName;

        @NotBlank(message = "nftSymbol은 필수 입력값입니다.")
        String nftSymbol;
    }
}
