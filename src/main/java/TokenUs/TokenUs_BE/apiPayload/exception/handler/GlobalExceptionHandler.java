package TokenUs.TokenUs_BE.apiPayload.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import TokenUs.TokenUs_BE.apiPayload.ApiResponse;
import TokenUs.TokenUs_BE.apiPayload.code.ErrorReasonDTO;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(GeneralException e) {
        ErrorReasonDTO reason = e.getErrorReasonHttpStatus();

        return ResponseEntity.status(reason.getHttpStatus())
                .body(ApiResponse.onFailure(reason.getCode(), reason.getMessage(), null));
    }

    // 예: NullPointerException 등 다른 예외도 처리 가능
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllUnexpected(Exception e) {
        e.printStackTrace(); // 로그 출력

        return ResponseEntity.status(500)
                .body(ApiResponse.onFailure("INTERNAL_SERVER_ERROR", "예기치 못한 오류가 발생했습니다.", null));
    }
}
