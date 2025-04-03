package TokenUs.TokenUs_BE.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import TokenUs.TokenUs_BE.apiPayload.code.BaseErrorCode;
import TokenUs.TokenUs_BE.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //    // ✅ 입력값 검증 실패 (VALIDATION_ERROR 추가)
    //    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION400", "입력값 검증 실패"),

    // 유저 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "유저가 이미 존재합니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4003", "닉네임은 필수 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4004", "비밀번호가 일치하지 않습니다."),
    ALREADY_SUBSCRIBED(HttpStatus.BAD_REQUEST, "USER4005", "이미 구독하였습니다."),
    SUBSCRIBE_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4006", "구독이 없습니다"),

    // 인증 관련
    NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "AUTH400", "인증되지 않은 요청입니다."),

    // 토큰 관련
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "옳바르지 않은 리프레시 토큰입니다."),

    // 비디오 관련
    VIDEO_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "VIDEO4001", "해당 영상이 이미 업로드 되어있습니다."),
    VIDEO_NOT_EXIST(HttpStatus.BAD_REQUEST, "VIDEO4002", "해당하는 영상이 없습니다."),
    NOT_YOUR_VIDEO(HttpStatus.BAD_REQUEST, "VIDEO4003", "로그인한 사용자의 영상이 아닙니다. 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
