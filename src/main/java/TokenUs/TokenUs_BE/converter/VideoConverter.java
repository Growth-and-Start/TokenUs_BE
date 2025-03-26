package TokenUs.TokenUs_BE.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                .user(user)
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

    public static VideoResponseDTO.similarityCheckResultDTO toCheckResult(String jsonBody) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonBody);

            JsonNode resultNode = root.get("similarity_check_result");
            if (resultNode == null || resultNode.isNull()) {
                throw new RuntimeException("similarity_check_result가 응답에 없습니다");
            }

            return VideoResponseDTO.similarityCheckResultDTO
                    .builder()
                    .videoPath(root.get("file_path").asText())
                    .downloadMessage(root.get("message").asText())
                    .maxSimilarity(resultNode.get("max_similarity").asDouble())
                    .avgSimilarity(resultNode.get("avg_similarity").asDouble())
                    .similarityMessage(resultNode.get("message").asText())
                    .similarVideoId(
                            resultNode.has("similar_video_id")
                                    ? resultNode.get("similar_video_id").asText()
                                    : null)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("유사도 응답 변환 실패: " + e.getMessage());
        }
    }
}
