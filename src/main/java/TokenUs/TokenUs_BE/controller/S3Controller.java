package TokenUs.TokenUs_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import TokenUs.TokenUs_BE.sevice.FlaskService;
import TokenUs.TokenUs_BE.sevice.S3Service;
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

    //    @PostMapping("video_upload_complete")
    //    @Operation(
    //            summary = "영상 s3업로드 완료 시 FE에서 API호출해서 백엔드에 전달url을 전달",
    //            description = "영상 유사도 검사를 위해 사용")
    //    public ResponseEntity<String> saveFile(@RequestBody Map<String, String> request) {
    //        String fileUrl = request.get("fileUrl");
    //
    //        // Flask 서버로 업로드된 파일 URL 전달
    //        flaskService.requestSimilarityCheck(fileUrl);
    //
    //        return ResponseEntity.ok("파일 업로드 및 url flask 전달");
    //    }
}
