package TokenUs.TokenUs_BE.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/video")
public class VideoController {

    @PostMapping("/similarity-check")
    @Operation(description = "flask에서 유사도 검사 결과를 반환")
    public ApiResponse<VideoResponseDTO.similarityCheckResultDTO> receiveSimilarityResult(
            @RequestBody VideoResponseDTO.similarityCheckResultDTO similarityCheckDTO) {
        System.out.println(
                "📡 Received similarity check result from Flask: {}" + similarityCheckDTO);

        // 유사도 검사 결과 처리
        if (similarityCheckDTO.isPassed()) {
            System.out.println("✅ Video passed similarity check. Ready for further processing.");
            // TODO: 유사도 검사를 통과한 영상 처리 (예: DB 저장, 처리 단계 업데이트 등)
        } else {
            System.out.println("❌ Video is too similar to existing content. Ignoring upload.");
            // TODO: 유사한 영상 처리 (예: 업로드 취소 로그 기록 등)
        }

        return ApiResponse.onSuccess(similarityCheckDTO);
    }
}
