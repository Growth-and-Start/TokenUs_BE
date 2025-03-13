package TokenUs.TokenUs_BE.apiPayload.exception.handler;

import TokenUs.TokenUs_BE.apiPayload.code.BaseErrorCode;
import TokenUs.TokenUs_BE.apiPayload.exception.GeneralException;

public class GeneralHandler extends GeneralException {

    public GeneralHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
