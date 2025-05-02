package TokenUs.TokenUs_BE.converter;

import java.math.BigInteger;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.Transaction;
import TokenUs.TokenUs_BE.dto.NftRequestDTO;
import TokenUs.TokenUs_BE.dto.NftResponseDTO;
import TokenUs.TokenUs_BE.repository.NftLikeRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Component
@RequiredArgsConstructor
public class NftConverter {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final NftLikeRepository nftLikeRepository;

    public Nft toNft(
            NftRequestDTO.NFTMintRequestDTO dto,
            BigInteger tokenId,
            Long userId,
            String nftName,
            String nftSymbol) {
        return Nft.builder()
                .tokenId(tokenId)
                .nftName(nftName)
                .nftSymbol(nftSymbol)
                .currentPrice(dto.getPrice())
                .mintPrice(dto.getPrice())
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
                                        nft.getNftName(),
                                        nft.getNftSymbol(),
                                        nft.getCurrentPrice(),
                                        nft.getVideo().getId(),
                                        nft.getOwner().getId(),
                                        nft.getIsListed(),
                                        nft.getMintQuantity()))
                .toList();
    }

    public List<NftResponseDTO.NFTTradeHistoryDTO> toTradeHistoryDTOList(
            List<Transaction> transactions) {
        return transactions.stream()
                .map(
                        transaction ->
                                NftResponseDTO.NFTTradeHistoryDTO.builder()
                                        .txHash(transaction.getTxHash())
                                        .tradePrice(transaction.getTradePrice())
                                        .createdAt(transaction.getCreatedAt().toString())
                                        .build())
                .toList();
    }

    public NftResponseDTO.listedNFTInfoDTO toListedNFTInfoDTO(
            Nft nft, String sellerWallet, Boolean isLiked) {
        return NftResponseDTO.listedNFTInfoDTO
                .builder()
                .id(nft.getId())
                .tokenId(nft.getTokenId())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .currentPrice(nft.getCurrentPrice())
                .videoId(nft.getVideo().getId())
                .isListed(nft.getIsListed())
                .creatorId(nft.getVideo().getCreator().getId())
                .sellerWallet(sellerWallet)
                .isLiked(isLiked)
                .likeCount(nftLikeRepository.countByNft(nft))
                .build();
    }

    public NftResponseDTO.NFTListResultDTO toNFTListResultDTO(Nft nft) {
        return NftResponseDTO.NFTListResultDTO.builder()
                .id(nft.getId())
                .tokenId(nft.getTokenId())
                .nftName(nft.getNftName())
                .nftSymbol(nft.getNftSymbol())
                .price(nft.getCurrentPrice())
                .currentPrice(nft.getCurrentPrice())
                .mintPrice(nft.getMintPrice())
                .sellerAddress(nft.getOwner().getWalletAddress())
                .isListed(nft.getIsListed())
                .likeCount(nftLikeRepository.countByNft(nft))
                .build();
    }

    public List<NftResponseDTO.NFTListResultDTO> toNFTListResultDTOList(List<Nft> nftList) {
        return nftList.stream().map(this::toNFTListResultDTO).toList();
    }
}
