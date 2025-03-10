package TokenUs.TokenUs_BE.converter;

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
                .title(request.getTitle())
                .detail(request.getDetail())
                .fileUrl(request.getFileUrl())
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
}
