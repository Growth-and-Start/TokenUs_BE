package TokenUs.TokenUs_BE.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import TokenUs.TokenUs_BE.apiPayload.code.BaseErrorCode;
import TokenUs.TokenUs_BE.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
