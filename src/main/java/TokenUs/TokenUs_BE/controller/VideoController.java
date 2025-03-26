package TokenUs.TokenUs_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final SimpMessagingTemplate messagingTemplate;
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

    @PostMapping("/similarity_check")
    @Operation(summary = "flask로 유사도 검사 요청")
    public ApiResponse<VideoResponseDTO.similarityCheckResultDTO> requestSimilarityCheck(
            @RequestBody VideoRequestDTO.similarityCheckRequestDTO request) {

        String fileUrl = request.getVideoUrl();
        System.out.println(fileUrl);

        // Flask 서버로 업로드된 파일 URL 전달
        String responseBody = flaskService.requestSimilarityCheck(fileUrl);
        VideoResponseDTO.similarityCheckResultDTO result =
                VideoConverter.toCheckResult(responseBody);

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/sand_result")
    @Operation(summary = "flask에서 유사도 검사 결과를 반환")
    public ResponseEntity<ApiResponse<VideoResponseDTO.similarityCheckResultDTO>>
            receiveSimilarityResult(
                    @Validated @RequestBody VideoResponseDTO.similarityCheckResultDTO result) {

        System.out.println("📡 Received similarity check result: " + result);

        //        // ✅ WebSocket을 통해 프론트엔드에 전송
        //        messagingTemplate.convertAndSend("/topic/similarity-result", result);

        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @PostMapping("/save")
    @Operation(summary = "영상 제목과 상세 정보 입력 및 메타데이터 DB저장")
    public ApiResponse<VideoResponseDTO.uploadResultDTO> saveVideoDetail(
            @Validated @RequestBody VideoRequestDTO.videoDetailRequestDTO request) {

        // 1. 로그인 유저
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 2. VideoService를 통해 Video 객체 생성 및 반환
        Video video = videoService.createVideo(request, user);

        VideoResponseDTO.uploadResultDTO response = videoConverter.toUploadResult(video);

        return ApiResponse.onSuccess(response);
    }
}
