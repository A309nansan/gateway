package site.nansan.gateway.exception;

import lombok.Getter;
import site.nansan.gateway.dto.ErrorCode;

@Getter
public class GatewayException extends RuntimeException {

    private final ErrorCode errorCode;

    public GatewayException(ErrorCode errorCode) {

        super(errorCode.getErrorMessage()); // 기본 메시지
        this.errorCode = errorCode;
    }

    public GatewayException(ErrorCode errorCode, String customMessage) {

        super(customMessage); // 커스텀 메시지 사용
        this.errorCode = errorCode;
    }
}
