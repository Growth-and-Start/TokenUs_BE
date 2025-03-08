package TokenUs.TokenUs_BE.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.converter.VideoConverter;
import TokenUs.TokenUs_BE.dto.VideoRequestDTO;
import TokenUs.TokenUs_BE.dto.VideoResponseDTO;
import org.json.JSONObject;

@RestController
@RequestMapping("/vidios")
public class VideoController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_SERVER_URL = "http://127.0.0.1:5000"; // Flask 서버 URL (배포 후 변경 예정)

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<VideoResponseDTO.uploadResponseDTO>> uploadVideo(
            @RequestParam("file") MultipartFile file) {

        // 1️⃣ DTO 변환 (Converter 사용)
        VideoRequestDTO.uploadRequestDTO requestDTO =
                VideoConverter.toUploadRequestDTO(file.getOriginalFilename());

        // 2️⃣ Flask에 유사도 검사 요청
        String flaskUrl = FLASK_SERVER_URL + "/check_similarity";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VideoRequestDTO.uploadRequestDTO> request =
                new HttpEntity<>(requestDTO, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(flaskUrl, HttpMethod.POST, request, String.class);

        // 3️⃣ Controller에서 response.getBody() 처리 & JSON 파싱
        JSONObject jsonResponse = new JSONObject(response.getBody());

        // 4️⃣ Flask 응답을 DTO로 변환
        VideoResponseDTO.uploadResponseDTO responseDTO =
                VideoConverter.toUploadResponseDTO(jsonResponse);

        // 5️⃣ 유사도가 너무 높으면 업로드 차단
        if (responseDTO.getError() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", responseDTO.getError(), null));
        }

        // 6️⃣ Flask에서 OK 응답이 오면 S3에 저장
        return ResponseEntity.ok(ApiResponse.onSuccess(responseDTO));
    }
}
