package TokenUs.TokenUs_BE.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.config.security.CustomUserDetails;
import TokenUs.TokenUs_BE.converter.VideoConverter;
import TokenUs.TokenUs_BE.converter.VideoLikeConverter;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.NftRepository;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.repository.VideoLikeRepository;
import TokenUs.TokenUs_BE.repository.VideoRepository;
import TokenUs.TokenUs_BE.service.FlaskService;
import TokenUs.TokenUs_BE.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired private final SimpMessagingTemplate messagingTemplate;

    private final VideoService videoService;
    private final UserRepository userRepository;
    private final VideoConverter videoConverter;
    private final FlaskService flaskService;
    private final NftRepository nftRepository;
    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoLikeConverter videoLikeConverter;

    public VideoController(
            SimpMessagingTemplate messagingTemplate,
            VideoService videoService,
            VideoConverter videoConverter,
            UserRepository userRepository,
            FlaskService flaskService,
            NftRepository nftRepository,
            VideoRepository videoRepository,
            VideoLikeRepository videoLikeRepository,
            VideoLikeConverter videoLikeConverter) {
        this.messagingTemplate = messagingTemplate;
        this.videoService = videoService;
        this.videoConverter = videoConverter;
        this.userRepository = userRepository;
        this.flaskService = flaskService;
        this.nftRepository = nftRepository;
        this.videoRepository = videoRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.videoLikeConverter = videoLikeConverter;
    }

    @GetMapping("/get_opened_videos")
    @Operation(summary = "공개된 영상 리스트 리턴", description = "기본정렬: 최신순")
    public ApiResponse<List<VideoResponseDTO.listResultDTO>> getVideoList(
            @RequestParam(required = false) Boolean subscribeFilter,
            @RequestParam(required = false) Boolean popularFilter) {

        User user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            user = userDetails.getUser();
        }

        List<Video> videos = videoService.getVideoList(user, subscribeFilter, popularFilter);

        List<VideoResponseDTO.listResultDTO> result =
                videos.stream().map(VideoConverter::toListResultDTO).collect(Collectors.toList());

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/search")
    @Operation(summary = "영상을 검색", description = "검색어와 일치하는 제목, 크리에이터이름을 가진 공개 비디오 최신순 정렬 반환")
    public ApiResponse<List<VideoResponseDTO.listResultDTO>> searchVideo(
            @RequestParam(required = true) String searchFor) {
        List<Video> videos = videoService.searchVideoList(searchFor);

        List<VideoResponseDTO.listResultDTO> result =
                videos.stream().map(VideoConverter::toListResultDTO).collect(Collectors.toList());

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/similarity_check")
    @Operation(summary = "flask로 유사도 검사 요청", description = "유사도 검사 요청만 보내고, 결과는 WebSocket으로 전송됨")
    public ApiResponse<String> requestSimilarityCheck(
            @RequestBody VideoRequestDTO.similarityCheckRequestDTO request) {

        String fileUrl = request.getVideoUrl();
        System.out.println("Flask로 유사도 검사 요청 시작: " + fileUrl);

        flaskService.sendSimilarityRequestAsync(fileUrl);

        return ApiResponse.onSuccess("유사도 검사 요청이 성공적으로 전송되었습니다.");
    }

    @PostMapping("/send_result")
    @Operation(summary = "flask에서 유사도 검사 결과를 반환", description = "flask 서버 사용. FE에서 사용X")
    public ResponseEntity<ApiResponse<VideoResponseDTO.similarityCheckResultDTO>>
            receiveSimilarityResult(
                    @Validated @RequestBody VideoResponseDTO.similarityCheckResultDTO result) {

        System.out.println("📡 Received similarity check result: " + result);

        messagingTemplate.convertAndSend("/topic/similarity_result", result);

        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @PostMapping("/save")
    @Operation(summary = "영상 제목과 상세 정보 입력 및 메타데이터 DB저장")
    public ApiResponse<VideoResponseDTO.uploadResultDTO> saveVideoDetail(
            @Validated @RequestBody VideoRequestDTO.videoDetailRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        Video video = videoService.createVideo(request, user);

        VideoResponseDTO.uploadResultDTO response = videoConverter.toUploadResult(video, user);

        return ApiResponse.onSuccess(response);
    }

    @GetMapping("/get_my_videos")
    @Operation(summary = "로그인한 사용자의 영상 리스트 반환", description = "영상 NFT의 currentPrice의 평균값 포함")
    public ApiResponse<List<VideoResponseDTO.listResultDTO>> getMyVideos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentUser = userDetails.getUser();

        List<VideoResponseDTO.listResultDTO> result =
                videoService.getUserVideoListWithNftAveragePrice(currentUser);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/modify")
    @Operation(
            summary = "비디오의 정보를 수정",
            description = "영상의 title, detail, thumbnailUrl, isOpen을 수정합니다.")
    public ApiResponse<VideoResponseDTO.modifyResultDTO> modifyVideoInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody(required = true) VideoRequestDTO.modifyRequestDTO request) {
        User user = userDetails.getUser();

        Video video = videoService.modifyVideo(request, user);

        VideoResponseDTO.modifyResultDTO result = videoConverter.toModifyResultDTO(video);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/check_nft")
    @Operation(summary = "유저가 videoUrl에 해당하는 NFT를 가지고 있는지 확인", description = "유사도 검사 실패시 요청")
    public ApiResponse<VideoResponseDTO.checkNftResultDTO> checkHavingNft(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = true) String videoUrl) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;

        Boolean owns = nftRepository.existsByVideo_FileUrlAndOwner_Id(videoUrl, userId);

        VideoResponseDTO.checkNftResultDTO responseDTO =
                new VideoResponseDTO.checkNftResultDTO(owns);

        return ApiResponse.onSuccess(responseDTO);
    }

    @GetMapping("get_url")
    @Operation(
            summary = "영상의 id로 video Url 반환",
            description = "유사도 검사 이후 유사한 영상의 id가 반환 되었을 때 사용합니다.")
    public ApiResponse<VideoResponseDTO.getVideoUrlDTO> getVideoUrlwithId(
            @RequestParam(required = true) Long videoId) {
        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 videoId를 가진 영상이 존재하지 않습니다."));

        String videoUrl = video.getFileUrl();

        VideoResponseDTO.getVideoUrlDTO responseDTO = new VideoResponseDTO.getVideoUrlDTO(videoUrl);

        return ApiResponse.onSuccess(responseDTO);
    }

    @GetMapping("detail")
    @Operation(summary = "영상의 id로 상세 정보 반환", description = "영상의 상세 시청 페이지에서 사용합니다.")
    public ApiResponse<VideoResponseDTO.getDetailDTO> getVideoDetail(
            @RequestParam(required = true) Long videoId) {

        Long currentUserId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            currentUserId = userDetails.getUser().getId();
        }

        Video video =
                videoRepository
                        .findById(videoId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "해당 videoId를 가진 영상이 존재하지 않습니다."));

        videoService.increaseView(videoId);
        Long likeCount = videoLikeRepository.countByVideo(video);

        Boolean isLiked = videoLikeRepository.existsByUserIdAndVideoId(currentUserId, videoId);

        VideoResponseDTO.getDetailDTO result =
                videoConverter.toDetailDTO(video, likeCount, isLiked);

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/like")
    @Operation(summary = "영상 좋아요 하기", description = "영상의 id를 넣고 요청하면 현재 로그인한 사용자가 like")
    public ApiResponse<VideoResponseDTO.likeResultDTO> likeVideo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = true) Long videoId) {
        Long userId = userDetails.getUser().getId();

        VideoLike videoLike = videoService.like(userId, videoId);

        VideoResponseDTO.likeResultDTO result = videoLikeConverter.toLikeResultDTO(videoLike, true);

        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/unlike")
    @Operation(summary = "영상 좋아요 취소", description = "영상의 id를 넣고 요청하면 현재 로그인한 사용자가 like 취소")
    public ApiResponse<VideoResponseDTO.likeResultDTO> unlikeVideo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = true) Long videoId) {
        Long userId = userDetails.getUser().getId();

        VideoLike videoLike = videoService.unlike(userId, videoId);

        VideoResponseDTO.likeResultDTO result =
                videoLikeConverter.toLikeResultDTO(videoLike, false);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/most_popular")
    @Operation(summary = "조회수 기준 가장 인기 영상 리턴", description = "")
    public ApiResponse<VideoResponseDTO.getDetailDTO> getMostPopularVideo() {
        Video video = videoService.getMostPopularVideo();

        Long likeCount = videoLikeRepository.countByVideo(video);

        VideoResponseDTO.getDetailDTO result = videoConverter.toDetailDTO(video, likeCount, false);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/listed")
    @Operation(
            summary = "Listed 비디오 조회",
            description =
                    "NFT가 listed된 비디오들의 기본 정보를 조회합니다. sort 파라미터: popular(인기순), interested(관심영상), 기본값(최신순)")
    public ApiResponse<List<VideoResponseDTO.listedVideoDTO>> getListedVideos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String sort) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        return ApiResponse.onSuccess(videoService.getListedVideos(userId, sort));
    }
}
