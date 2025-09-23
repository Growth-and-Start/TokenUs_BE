package TokenUs.TokenUs_BE.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import TokenUs.TokenUs_BE.apiPayload.code.BaseErrorCode;
import TokenUs.TokenUs_BE.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // General Errors
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "Server Error."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "Bad Request."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "Unauthorized."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "Forbidden."),

    // User-related errors
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "User not found."),
    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "User already exists."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4003", "Nickname is required."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4004", "Invalid password."),
    ALREADY_SUBSCRIBED(HttpStatus.BAD_REQUEST, "USER4005", "Already subscribed."),
    SUBSCRIBE_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4006", "Subscription not found."),
    NICKNAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "USER4002", "Nickname already exists."),

    // Authentication and Authorization
    NOT_AUTHORIZED(HttpStatus.BAD_REQUEST, "AUTH400", "Unauthorized request."),

    // Token-related errors
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "Invalid refresh token."),

    // Video-related errors
    VIDEO_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "VIDEO4001", "Video already exists."),
    VIDEO_NOT_EXIST(HttpStatus.BAD_REQUEST, "VIDEO4002", "Video not found."),
    NOT_YOUR_VIDEO(HttpStatus.BAD_REQUEST, "VIDEO4003", "Not your video."),
    ALREADY_LIKED(HttpStatus.BAD_REQUEST, "VIDEO4004", "Already liked."),
    LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "VIDEO4005", "Like not found.");

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
