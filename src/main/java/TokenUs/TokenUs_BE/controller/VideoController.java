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
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import TokenUs.TokenUs_BE.repository.UserRepository;
import TokenUs.TokenUs_BE.sevice.FlaskService;
import TokenUs.TokenUs_BE.sevice.VideoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired private final SimpMessagingTemplate messagingTemplate;

    private final VideoService videoService;
    private final UserRepository userRepository;
    private final VideoConverter videoConverter;
    private final FlaskService flaskService;

    public VideoController(
            SimpMessagingTemplate messagingTemplate,
            VideoService videoService,
            VideoConverter videoConverter,
            UserRepository userRepository,
            FlaskService flaskService) {
        this.messagingTemplate = messagingTemplate;
        this.videoService = videoService;
        this.videoConverter = videoConverter;
        this.userRepository = userRepository;
        this.flaskService = flaskService;
    }

    @GetMapping("/get_opened_videos")
    @Operation(summary = "공개된 영상 리스트 리턴", description = "기본정렬: 최신순")
    public ApiResponse<List<VideoResponseDTO.listResultDTO>> getVideoList(
            @RequestParam(required = false) Boolean isSubscribe) {

        User user = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            user = userDetails.getUser();
        }

        List<Video> videos = videoService.getVideoList(user, isSubscribe);

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

    //    @PostMapping("/similarity_check")
    //    @Operation(
    //            summary = "flask로 유사도 검사 요청",
    //            description =
    //                    " *250326*유사도 검사 걸렸을 때 nft 보유 여부 아직 확인하지 않고 있음. 추후 추가 예정<br> 유사한 영상은 현재
    // id만 반환중, 추후 만들어질 id로 영상 상세 페이지 get API구현 되어야함")
    //    public ApiResponse<VideoResponseDTO.similarityCheckResultDTO> requestSimilarityCheck(
    //            @RequestBody VideoRequestDTO.similarityCheckRequestDTO request) {
    //
    //        String fileUrl = request.getVideoUrl();
    //        System.out.println(fileUrl);
    //
    //        // Flask 서버로 업로드된 파일 URL 전달
    //        String responseBody = flaskService.requestSimilarityCheck(fileUrl);
    //        VideoResponseDTO.similarityCheckResultDTO result =
    //                VideoConverter.toCheckResult(responseBody);
    //
    //        return ApiResponse.onSuccess(result);
    //    }

    @PostMapping("/similarity_check")
    @Operation(summary = "flask로 유사도 검사 요청", description = "유사도 검사 요청만 보내고, 결과는 WebSocket으로 전송됨")
    public ApiResponse<String> requestSimilarityCheck(
            @RequestBody VideoRequestDTO.similarityCheckRequestDTO request) {

        String fileUrl = request.getVideoUrl();
        System.out.println("📤 Flask로 유사도 검사 요청 시작: " + fileUrl);

        // ✅ Flask 서버로 비동기 요청 전송 (응답 기다리지 않음)
        flaskService.sendSimilarityRequestAsync(fileUrl);

        // ✅ 즉시 응답
        return ApiResponse.onSuccess("유사도 검사 요청이 성공적으로 전송되었습니다.");
    }

    @PostMapping("/send_result")
    @Operation(summary = "flask에서 유사도 검사 결과를 반환", description = "flask 서버 사용. FE에서 사용X")
    public ResponseEntity<ApiResponse<VideoResponseDTO.similarityCheckResultDTO>>
            receiveSimilarityResult(
                    @Validated @RequestBody VideoResponseDTO.similarityCheckResultDTO result) {

        System.out.println("📡 Received similarity check result: " + result);

        // ✅ WebSocket으로 결과 전송
        // TODO: 유저에 따라 다르게 구현
        messagingTemplate.convertAndSend("/topic/similarity_result", result);

        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @PostMapping("/save")
    @Operation(summary = "영상 제목과 상세 정보 입력 및 메타데이터 DB저장")
    public ApiResponse<VideoResponseDTO.uploadResultDTO> saveVideoDetail(
            @Validated @RequestBody VideoRequestDTO.videoDetailRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. 로그인 유저
        User user = userDetails.getUser();

        // 2. VideoService를 통해 Video 객체 생성 및 반환
        Video video = videoService.createVideo(request, user);

        VideoResponseDTO.uploadResultDTO response = videoConverter.toUploadResult(video);

        return ApiResponse.onSuccess(response);
    }

    // 필요 API->내 영상 리스트 반환, 공개 여부 수정, 현재 로그인한 사용자 이메일 받아오기, 지갑 주소 있는지 확인
    @GetMapping("/get_my_videos")
    @Operation(summary = "로그인한 사용자의 영상 리스트 반환", description = "영상 NFT의 currentPrice의 평균값 포함")
    public ApiResponse<List<VideoResponseDTO.listResultDTO>> getMyVideos(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // user 객체 반환
        User currentUser = userDetails.getUser();

        List<VideoResponseDTO.listResultDTO> result =
                videoService.getUserVideoListWithNftAveragePrice(currentUser);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/to_open")
    @Operation(summary = "비디오를 비공개에서 공개로 전환", description = "")
    public ApiResponse<VideoResponseDTO.openResultDTO> toOpenVideo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = true) Long videoId) {
        // 현재 로그인한 사용자가 크리에이터가 맞는지 확인
        User user = userDetails.getUser();

        Video video = videoService.openVideo(videoId, user);

        VideoResponseDTO.openResultDTO result = videoConverter.toOpenResultDTO(video);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/to_close")
    @Operation(summary = "비디오를 공개에서 비공개로 전환", description = "")
    public ApiResponse<VideoResponseDTO.openResultDTO> toCloseVideo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = true) Long videoId) {
        // 현재 로그인한 사용자가 크리에이터가 맞는지 확인
        User user = userDetails.getUser();

        Video video = videoService.closeVideo(videoId, user);

        VideoResponseDTO.openResultDTO result = videoConverter.toOpenResultDTO(video);

        return ApiResponse.onSuccess(result);
    }
}
