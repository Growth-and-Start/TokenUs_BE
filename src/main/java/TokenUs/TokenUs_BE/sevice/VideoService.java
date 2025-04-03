package TokenUs.TokenUs_BE.sevice;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.apiPayload.code.status.ErrorStatus;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;
import TokenUs.TokenUs_BE.converter.VideoConverter;
import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final VideoConverter videoconverter;
    private final VideoConverter videoConverter;

    public Video createVideo(VideoRequestDTO.videoDetailRequestDTO request, User user) {

        // 해당 file_url의 영상이 이미 업로드 되었다면
        if (videoRepository.findByFileUrl((request.getVideoUrl())).isPresent()) {
            throw new GeneralException(ErrorStatus.VIDEO_ALREADY_EXIST);
        }

        Video newVideo = videoConverter.toVideo(request, user);

        return videoRepository.save(newVideo);
    }

    public List<Video> getVideoList(User user, Boolean isSubscribe) {
        // 구독 필터링 요청인 경우
        if (Boolean.TRUE.equals(isSubscribe)) {
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
}
