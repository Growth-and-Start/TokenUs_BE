package TokenUs.TokenUs_BE.dto;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

public class VideoResponseDTO {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class listResultDTO {

        private int videoId;
        private String videoTitle;
        private String videoUrl;
        private String videoDetail;
        private LocalDateTime createdAt;
        private String thumbnailUrl;
        private Long creatorId;
        private String creatorNickname;
        private BigInteger nftPrice;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class similarityCheckResultDTO {

        @JsonProperty("video_url")
        private String videoUrl;

        @JsonProperty("message")
        private String similarityMessage;

        @JsonProperty("max_segment_similarity")
        private double maxSegmentSimilarity;

        @JsonProperty("elapsed_time")
        private float elapsedTime;

        @JsonProperty("passed")
        private boolean passed;

        @JsonProperty("similar_video_url")
        private String similarVideoUrl;

        private Long similarVideoId;

        private List<String> tokenIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class uploadResultDTO {

        private Long id;

        @JsonProperty("video_path")
        private String videoPath;

        private List<Long> parentVideoTokenId;

        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class openResultDTO {

        private Long id;

        private Boolean isOpened;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class modifyResultDTO {

        private Long id;
        private String videoTitle;
        private String videoDetail;
        private String thumbnailUrl;
        private Boolean isOpen;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class checkNftResultDTO {

        private Boolean hasNft;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getVideoUrlDTO {

        private String videoUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getDetailDTO {
        private Long videoId;
        private String videoTitle;
        private String videoUrl;
        private String videoDetail;
        private LocalDateTime createdAt;
        private String thumbnailUrl;
        private Long creatorId;
        private Long likeCount;
        private Long viewCount;
        private Boolean isLiked;
        private BigInteger mintPrice;
        private BigInteger floorPrice;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class likeResultDTO {
        private Long videoId;
        private Boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class listedVideoDTO {
        private Long videoId;
        private String videoUrl;
        private String title;
        private String detail;
        private Long creatorId;
        private String creatorNickname;
        private String creatorProfileUrl;
        private String thumbnailUrl;
        private LocalDateTime createdAt;
        private Boolean isInterested;
        private Long interestCount;
        private BigInteger floorPrice;
    }
}
