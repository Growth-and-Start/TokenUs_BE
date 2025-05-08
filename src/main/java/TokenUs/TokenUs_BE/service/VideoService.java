package TokenUs.TokenUs_BE.service;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;
import TokenUs.TokenUs_BE.converter.VideoConverter;
import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoLikeRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final VideoConverter videoconverter;
    private final VideoConverter videoConverter;
    private final VideoLikeRepository videoLikeRepository;

    public Video createVideo(VideoRequestDTO.videoDetailRequestDTO request, User user) {

        // 해당 file_url의 영상이 이미 업로드 되었다면
        if (videoRepository.findByFileUrl((request.getVideoUrl())).isPresent()) {
            throw new GeneralException(ErrorStatus.VIDEO_ALREADY_EXIST);
        }

        Video newVideo = videoConverter.toVideo(request, user);

        return videoRepository.save(newVideo);
    }

    public List<Video> getVideoList(User user, Boolean subscribeFilter, Boolean popularFilter) {
        // 구독 필터링 요청인 경우
        if (Boolean.TRUE.equals(subscribeFilter)) {
            if (user == null) {
                // 로그인하지 않은 유저가 구독 필터링 요청 → 빈 리스트 반환
                return Collections.emptyList();
            }

            List<User> subscribedToList =
                    user.getSubscribingList().stream()
                            .map(Subscribe::getTarget)
                            .collect(Collectors.toList());

            return videoRepository.findByCreatorInAndIsOpenTrueOrderByCreatedAtDesc(
                    subscribedToList);
        }

        // 인기순 정렬 요청인 경우
        if (Boolean.TRUE.equals(popularFilter)) {
            // 인기순 정렬 로직 추가
            return videoRepository.findAllByIsOpenTrueOrderByViewsDesc();
        }

        // 전체 공개 영상 최신순 정렬
        return videoRepository.findAllByIsOpenTrueOrderByCreatedAtDesc();
    }

    public List<Video> searchVideoList(String searchFor) {

        return videoRepository.searchByTitleOrCreatorNickname(searchFor);
    }

    public List<VideoResponseDTO.listResultDTO> getUserVideoListWithNftAveragePrice(User user) {
        List<Video> videos = videoRepository.findByCreatorOrderByCreatedAtDesc(user);

        return videos.stream()
                .map(
                        video -> {
                            // 평균 NFT 가격 계산
                            BigInteger avgPrice = null;
                            List<Nft> nftList = video.getNfts();
                            if (nftList != null && !nftList.isEmpty()) {
                                BigInteger total =
                                        nftList.stream()
                                                .map(Nft::getCurrentPrice)
                                                .reduce(BigInteger.ZERO, BigInteger::add);

                                avgPrice = total.divide(BigInteger.valueOf(nftList.size()));
                            }

                            // DTO 변환
                            return VideoConverter.toUserListResultDTO(video, avgPrice);
                        })
                .collect(Collectors.toList());
    }

    public Video openVideo(Long videoId, User user) {
        // 영상이 존재하는지 검증
        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_YOUR_VIDEO));

        // 로그인한 사용자의 영상이 맞는지 검증
        if (!video.getCreator().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.NOT_YOUR_VIDEO);
        }

        video.setIsOpen(true);
        videoRepository.save(video);
        return video;
    }

    public Video closeVideo(Long videoId, User user) {
        // 영상이 존재하는지 검증
        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_YOUR_VIDEO));

        // 로그인한 사용자의 영상이 맞는지 검증
        if (!video.getCreator().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus.NOT_YOUR_VIDEO);
        }

        video.setIsOpen(false);
        videoRepository.save(video);
        return video;
    }

    @Transactional
    public void increaseView(Long videoId) {
        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new RuntimeException("해당 영상이 존재하지 않습니다."));
        video.setViews(video.getViews() + 1);
    }

    public VideoLike like(Long userId, Long videoId) {
        if (videoLikeRepository.existsByUserIdAndVideoId(userId, videoId)) {
            throw new GeneralException(ErrorStatus.ALREADY_LIKED);
        }

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.VIDEO_NOT_EXIST));

        VideoLike videoLike = VideoLike.builder().video(video).user(user).build();

        return videoLikeRepository.save(videoLike);
    }

    public VideoLike unlike(Long userId, Long videoId) {
        VideoLike videoLike =
                videoLikeRepository
                        .findByUserIdAndVideoId(userId, videoId)
                        .orElseThrow(() -> new GeneralException(ErrorStatus.LIKE_NOT_FOUND));

        videoLikeRepository.delete(videoLike);

        return videoLike; // 삭제된 객체 반환 (원한다면 여기서 null 처리도 가능)
    }

    public Video getMostPopularVideo() {
        // 공개 영상 중, views가 가장 높은 영상 1개
        Video mostPopularVideo = videoRepository.findTopByIsOpenTrueOrderByViewsDesc();

        if (mostPopularVideo == null) {
            throw new GeneralException(ErrorStatus.VIDEO_NOT_EXIST);
        }

        return mostPopularVideo;
    }

    public List<VideoResponseDTO.listedVideoDTO> getListedVideos(Long userId, String sort) {
        List<Video> videos = videoRepository.findAll();

        List<VideoResponseDTO.listedVideoDTO> result =
                videos.stream()
                        .filter(
                                video ->
                                        video.getNfts().stream().anyMatch(nft -> nft.getIsListed()))
                        .map(
                                video -> {
                                    Boolean isInterested =
                                            userId != null
                                                    && videoLikeRepository.existsByUserIdAndVideoId(
                                                            userId, video.getId());
                                    Long interestCount = videoLikeRepository.countByVideo(video);

                                    // listed된 NFT 중 가장 낮은 가격 계산
                                    BigInteger floorPrice =
                                            video.getNfts().stream()
                                                    .filter(nft -> nft.getIsListed())
                                                    .map(nft -> nft.getCurrentPrice())
                                                    .min(BigInteger::compareTo)
                                                    .orElse(BigInteger.ZERO);

                                    return VideoConverter.toListedVideoDTO(
                                            video, isInterested, interestCount, floorPrice);
                                })
                        .collect(Collectors.toList());

        // 정렬 처리
        if (sort != null) {
            switch (sort.toLowerCase()) {
                case "popular":
                    // 인기순 정렬 (interestCount 기준 내림차순)
                    result.sort((a, b) -> b.getInterestCount().compareTo(a.getInterestCount()));
                    break;
                case "interested":
                    // 관심 영상만 필터링 (로그인한 사용자가 있는 경우에만)
                    if (userId != null) {
                        result =
                                result.stream()
                                        .filter(video -> video.getIsInterested())
                                        .collect(Collectors.toList());
                    }
                    break;
                default:
                    // 기본값: 최신순 정렬 (createdAt 기준 내림차순)
                    result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            }
        } else {
            // 기본값: 최신순 정렬
            result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        return result;
    }
}
