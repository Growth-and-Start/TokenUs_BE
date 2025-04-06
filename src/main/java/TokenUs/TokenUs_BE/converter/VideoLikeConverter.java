package TokenUs.TokenUs_BE.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.domain.mapping.VideoLike;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;

@Component
@RequiredArgsConstructor
public class VideoLikeConverter {

    public static VideoResponseDTO.likeResultDTO toLikeResultDTO(
            VideoLike videoLike, Boolean isLike) {

        return VideoResponseDTO.likeResultDTO
                .builder()
                .isLiked(isLike)
                .videoId(videoLike.getVideo().getId())
                .build();
    }
}
