package TokenUs.TokenUs_BE.converter;

import java.math.BigInteger;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;

@Component
@RequiredArgsConstructor
public class VideoConverter {

    public static Video toVideo(VideoRequestDTO.videoDetailRequestDTO request, User user) {

        return Video.builder()
                .title(request.getVideoTitle())
                .detail(request.getVideoDetail())
                .fileUrl(request.getVideoUrl())
                .isOpen(request.getIsOpen())
                .thumbnailUrl(request.getThumbnailUrl())
                .creator(user)
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

    public static VideoResponseDTO.uploadResultDTO toUploadResult(Video video) {

        return VideoResponseDTO.uploadResultDTO
                .builder()
                .id(video.getId())
                .videoPath(video.getFileUrl())
                .createdAt(video.getCreatedAt())
                .build();
    }

    //    public static VideoResponseDTO.similarityCheckResultDTO toCheckResult(String json) {
    //        try {
    //            ObjectMapper mapper = new ObjectMapper();
    //            JsonNode root = mapper.readTree(json);
    //
    //            // Flask의 /download 응답 구조:
    //            // {
    //            //   "message": "Download successful",
    //            //   "video_url": "...",
    //            //   "similarity_check_result": {
    //            //       "max_similarity": ...,
    //            //       "avg_similarity": ...,
    //            //       "message": "...",
    //            //       "passed": true,
    //            //       "similar_video_id": "3" (optional)
    //            //   }
    //            // }
    //
    //            JsonNode sim = root.path("similarity_check_result");
    //
    //            return VideoResponseDTO.similarityCheckResultDTO
    //                    .builder()
    //                    .videoUrl(root.path("video_url").asText(null))
    //                    .maxSimilarity(sim.path("max_similarity").asDouble(0.0))
    //                    .avgSimilarity(sim.path("avg_similarity").asDouble(0.0))
    //                    .similarityMessage(sim.path("message").asText(null))
    //                    .passed(sim.path("passed").asBoolean(false))
    //                    .similarVideoId(
    //                            sim.has("similar_video_id")
    //                                    ? sim.get("similar_video_id").asText(null)
    //                                    : null)
    //                    .build();
    //
    //        } catch (Exception e) {
    //            throw new RuntimeException("Flask 응답 JSON 파싱 실패", e);
    //        }
    //    }

    public static VideoResponseDTO.openResultDTO toOpenResultDTO(Video video) {
        return VideoResponseDTO.openResultDTO
                .builder()
                .id(video.getId())
                .isOpened(video.getIsOpen())
                .build();
    }

    public static VideoResponseDTO.getDetailDTO toDetailDTO(
            Video video, Long likeCount, Boolean isLiked) {
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
                .viewCount(video.getViews())
                .isLiked(isLiked)
                .build();
    }
}
