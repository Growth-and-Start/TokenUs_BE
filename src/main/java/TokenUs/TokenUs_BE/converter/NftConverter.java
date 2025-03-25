package TokenUs.TokenUs_BE.converter;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Component
@RequiredArgsConstructor
public class NftConverter {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public Nft toNft(
            NftRequestDTO.NFTMintRequestDTO dto,
            BigInteger tokenId,
            String contractAddress,
            Long userId) {
        return Nft.builder()
                .tokenId(tokenId)
                .contractAddress(contractAddress)
                .currentPrice(dto.getPrice())
                .mintQuantity(dto.getTotalSupply())
                .isListed(false) // 민팅 시엔 기본 false
                .owner(userRepository.getReferenceById(userId))
                .video(videoRepository.getReferenceById(dto.getVideoId().longValue()))
                .build();
    }

    public List<NftResponseDTO.NFTInfoDTO> toDTOList(List<Nft> nftList) {
        return nftList.stream()
                .map(
                        nft ->
                                new NftResponseDTO.NFTInfoDTO(
                                        nft.getTokenId(),
                                        nft.getCurrentPrice(),
                                        nft.getContractAddress(),
                                        nft.getVideo().getId(),
                                        nft.getOwner().getId(),
                                        nft.getIsListed(),
                                        nft.getMintQuantity()))
                .toList();
    }
}
