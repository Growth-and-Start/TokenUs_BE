package TokenUs.TokenUs_BE.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.sevice.S3Service;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/presigned-url")
    @Operation(description = "S3 업로드용 presigned-url, 유효기간 10분")
    public ResponseEntity<String> getPresignedUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "application/octet-stream") String contentType) {

        String presignedUrl = s3Service.generatePresignedUrl(fileName, contentType);
        return ResponseEntity.ok(presignedUrl);
    }

    @PostMapping("save-file")
    @Operation(description = "s3업로드 완료 시 url을 백엔드에 전달")
    public ResponseEntity<String> saveFile(@RequestBody Map<String, String> request) {
        String fileUrl = request.get("fileUrl");

        System.out.println("파일 업로드 완료: " + fileUrl);

        return ResponseEntity.ok("파일 저장 완료");
    }
}
