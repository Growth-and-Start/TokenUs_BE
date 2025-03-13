package TokenUs.TokenUs_BE.apiPayload.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

// API 응답의 이유와 관련된 정보를 담음

@Getter
@Builder
public class ReasonDTO {

    private HttpStatus httpStatus;

    private final boolean isSuccess;
    private final String code;
    private final String message;

    public boolean getIsSuccess() {
        return isSuccess;
    }
}
