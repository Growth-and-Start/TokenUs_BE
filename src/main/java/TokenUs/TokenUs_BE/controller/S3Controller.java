package TokenUs.TokenUs_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.service.FlaskService;
import TokenUs.TokenUs_BE.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    private final FlaskService flaskService;

    @GetMapping("/presigned_url")
    @Operation(summary = "S3 업로드용 presigned-url, 유효기간 10분")
    public ResponseEntity<String> getPresignedUrl(
            @RequestParam String folder, // "video", "profile", "thumbnail"
            @RequestParam String fileName,
            @RequestParam(defaultValue = "application/octet-stream") String contentType) {

        String presignedUrl = s3Service.generatePresignedUrl(folder, fileName, contentType);
        return ResponseEntity.ok(presignedUrl);
    }
}
