package TokenUs.TokenUs_BE.converter;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import org.json.JSONObject;

@Component
@RequiredArgsConstructor
public class VideoConverter {

    // 📌 MultipartFile → DTO 변환
    public static VideoRequestDTO.uploadRequestDTO toUploadRequestDTO(String filename) {
        return VideoRequestDTO.uploadRequestDTO.builder().filename(filename).build();
    }

    // 📌 Flask 응답 → DTO 변환
    public static VideoResponseDTO.uploadResponseDTO toUploadResponseDTO(JSONObject jsonResponse) {
        return VideoResponseDTO.uploadResponseDTO
                .builder()
                .message(jsonResponse.optString("message", null))
                .s3Url(jsonResponse.optString("s3_url", null))
                .error(jsonResponse.optString("error", null))
                .similarity(
                        jsonResponse.has("similarity")
                                ? jsonResponse.getDouble("similarity")
                                : null)
                .build();
    }
}
