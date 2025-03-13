package TokenUs.TokenUs_BE.dto;

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

        String transactionHash;

        Float gasUsed;

        String status;
    }
}
