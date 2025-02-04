package TokenUs.TokenUs_BE.apiPayload.code;

// 구체화하는 Status에서 두개의 메소드를 반드시 Override할 것을 강제하는 역할

public interface BaseCode {

    ReasonDTO getReason();

    ReasonDTO getReasonHttpStatus();
}
