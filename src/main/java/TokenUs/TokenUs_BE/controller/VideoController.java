package TokenUs.TokenUs_BE.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import TokenUs.TokenUs_BE.sevice.VideoService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/video")
public class VideoController {

    private final SimpMessagingTemplate messagingTemplate;
    private final VideoService videoService;
    private final UserRepository userRepository;
    private final VideoConverter videoConverter;

    public VideoController(
            SimpMessagingTemplate messagingTemplate,
            VideoService videoService,
            VideoConverter videoConverter,
            UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.videoService = videoService;
        this.videoConverter = videoConverter;
        this.userRepository = userRepository;
    }

    @PostMapping("/similarity-check")
    @Operation(description = "flask에서 유사도 검사 결과를 반환")
    public ResponseEntity<ApiResponse<VideoResponseDTO.similarityCheckResultDTO>>
            receiveSimilarityResult(
                    @Validated @RequestBody VideoResponseDTO.similarityCheckResultDTO result) {

        System.out.println("📡 Received similarity check result: " + result);

        // ✅ WebSocket을 통해 프론트엔드에 전송
        messagingTemplate.convertAndSend("/topic/similarity-result", result);

        return ResponseEntity.ok(ApiResponse.onSuccess(result)); // ✅ 프론트에 결과 반환
    }

    // 일단은 detail을 받아서 바로 영상 메타데이터를 mysql에 저장하도록
    // TODO: detail 받아서 nft 발급도 함께 + 썸네일 이미지
    @PostMapping("/upload_detail")
    @Operation(description = "유사도 검사를 통과했을 시, 영상 제목과 상세 정보 입력 및 메타데이터 DB저장")
    public ApiResponse<VideoResponseDTO.uploadResultDTO> saveVideoDetail(
            @Validated @RequestBody VideoRequestDTO.videoDetailRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        User user = optionalUser.get();

        Video video = videoService.saveDetail(request, user);

        VideoResponseDTO.uploadResultDTO response = videoConverter.toUploadResult(video);

        return ApiResponse.onSuccess(response);
    }
}
