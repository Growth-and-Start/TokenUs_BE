package TokenUs.TokenUs_BE.converter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.NftRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Component
@RequiredArgsConstructor
public class VideoConverter {

    private final VideoRepository videoRepository;
    private final NftRepository nftRepository;

    public Video toVideo(VideoRequestDTO.videoDetailRequestDTO request, User user) {

        Video parentVideo = null;

        if (request.getParentVideoId() != null) {
            parentVideo =
                    videoRepository
                            .findById(request.getParentVideoId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_EXIST));
        }

        return Video.builder()
                .title(request.getVideoTitle())
                .detail(request.getVideoDetail())
                .fileUrl(request.getVideoUrl())
                .isOpen(request.getIsOpen())
                .thumbnailUrl(request.getThumbnailUrl())
                .creator(user)
                .parentVideo(parentVideo)
                .views(0L)
                .build();
    }

    public static VideoResponseDTO.listResultDTO toListResultDTO(Video video) {
        return VideoResponseDTO.listResultDTO
                .builder()
                .videoId(video.getId().intValue())
                .videoTitle(video.getTitle())
                .videoUrl(video.getFileUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .createdAt(video.getCreatedAt())
                .creatorId(video.getCreator().getId())
                .creatorNickname(video.getCreator().getNickname())
                .build();
    }

    public static VideoResponseDTO.listResultDTO toUserListResultDTO(
            Video video, BigInteger averagePrice) {
        return VideoResponseDTO.listResultDTO
                .builder()
                .videoId(video.getId().intValue())
                .videoTitle(video.getTitle())
                .videoUrl(video.getFileUrl())
                .videoDetail(video.getDetail())
                .thumbnailUrl(video.getThumbnailUrl())
                .createdAt(video.getCreatedAt())
                .creatorId(video.getCreator().getId())
                .creatorNickname(video.getCreator().getNickname())
                .nftPrice(averagePrice)
                .build();
    }

    public VideoResponseDTO.uploadResultDTO toUploadResult(Video video, User user) {

        List<Long> parentTokenIds = new ArrayList<>();

        if (video.getParentVideo() != null) {
            parentTokenIds =
                    nftRepository.findTokenIdsByVideoAndOwner(video.getParentVideo(), user);
        }

        return VideoResponseDTO.uploadResultDTO
                .builder()
                .id(video.getId())
                .videoPath(video.getFileUrl())
                .parentVideoTokenId(parentTokenIds)
                .createdAt(video.getCreatedAt())
                .build();
    }

    public static VideoResponseDTO.openResultDTO toOpenResultDTO(Video video) {
        return VideoResponseDTO.openResultDTO
                .builder()
                .id(video.getId())
                .isOpened(video.getIsOpen())
                .build();
    }

    public static VideoResponseDTO.modifyResultDTO toModifyResultDTO(Video video) {
        return VideoResponseDTO.modifyResultDTO
                .builder()
                .id(video.getId())
                .videoTitle(video.getTitle())
                .videoDetail(video.getDetail())
                .thumbnailUrl(video.getThumbnailUrl())
                .isOpen(video.getIsOpen())
                .build();
    }

    public static VideoResponseDTO.getDetailDTO toDetailDTO(
            Video video, Long likeCount, Boolean isLiked) {
        BigInteger mintPrice = null;
        BigInteger floorPrice = null;

        if (video.getNfts() != null && !video.getNfts().isEmpty()) {
            mintPrice = video.getNfts().get(0).getMintPrice();

            System.out.println("Total NFTs: " + video.getNfts().size());
            System.out.println(
                    "Listed NFTs: "
                            + video.getNfts().stream().filter(nft -> nft.getIsListed()).count());
            System.out.println(
                    "Current prices: "
                            + video.getNfts().stream()
                                    .filter(nft -> nft.getIsListed())
                                    .map(nft -> nft.getCurrentPrice())
                                    .collect(java.util.stream.Collectors.toList()));

            floorPrice =
                    video.getNfts().stream()
                            .filter(nft -> nft.getIsListed())
                            .map(nft -> nft.getCurrentPrice())
                            .min(BigInteger::compareTo)
                            .orElse(null);
        }

        return VideoResponseDTO.getDetailDTO
                .builder()
                .videoId(video.getId())
                .videoTitle(video.getTitle())
                .videoDetail(video.getDetail())
                .videoUrl(video.getFileUrl())
                .createdAt(video.getCreatedAt())
                .thumbnailUrl(video.getThumbnailUrl())
                .creatorId(video.getCreator().getId())
                .likeCount(likeCount)
                .parentVideoId(video.getParentVideo().getId())
                .viewCount(video.getViews())
                .isLiked(isLiked)
                .mintPrice(mintPrice)
                .floorPrice(floorPrice)
                .build();
    }

    public static VideoResponseDTO.listedVideoDTO toListedVideoDTO(
            Video video, Boolean isInterested, Long interestCount, BigInteger floorPrice) {
        return VideoResponseDTO.listedVideoDTO
                .builder()
                .videoId(video.getId())
                .videoUrl(video.getFileUrl())
                .title(video.getTitle())
                .detail(video.getDetail())
                .creatorId(video.getCreator().getId())
                .creatorNickname(video.getCreator().getNickname())
                .creatorProfileUrl(video.getCreator().getProfile_image())
                .thumbnailUrl(video.getThumbnailUrl())
                .createdAt(video.getCreatedAt())
                .isInterested(isInterested)
                .interestCount(interestCount)
                .floorPrice(floorPrice)
                .build();
    }
}
