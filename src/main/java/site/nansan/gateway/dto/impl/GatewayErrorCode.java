package site.nansan.gateway.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import site.nansan.gateway.dto.ErrorCode;

@Getter
@AllArgsConstructor
public enum GatewayErrorCode implements ErrorCode {

    // 4XX: Client Error
    URL_INVALID_FORMAT(HttpStatus.UNAUTHORIZED, "CLIENT_400_1", "잘못된 URL 요청입니다."),

    JWT_VALIDATION_FAILED(HttpStatus.UNAUTHORIZED, "CLIENT_401_1", "유효하지 않는 JWT 서명입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "CLIENT_401_2", "만료된 JWT 토큰입니다."),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "CLIENT_401_3", "지원하지 않는 JWT 토큰입니다."),
    JWT_CLAIMS_EMPTY(HttpStatus.UNAUTHORIZED, "CLIENT_401_4", "잘못된 JWT 토큰입니다."),
    JWT_UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "CLIENT_401_4", "알 수 없는 JWT 오류입니다."),

    // 5XX: Server Error
    NULL_ATTRIBUTE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_1", "존재하지 않는 Key로 검색했습니다."),
    NULL_HEADER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_2", "존재하지 않는 Key로 검색했습니다."),
    ILLEGAL_INDEX_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_3", "인덱스 값이 유효하지 않습니다."),
    JSON_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_4", "Json 변환에 실패했습니다."),
    PASSPORT_HMAC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_5", "Passport 서명에 실패했습니다."),
    JSON_CONVERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_6", "Json 변환 중에 오류가 발생했습니다."),
    NO_ALGORITHM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_7", "서명 알고리즘을 찾을 수 없습니다."),
    INVALID_KEY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_500_8", "유효하지 않은 키 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GATEWAY_5XX_1", "서버가 혼잡합니다. 다시 시도해 주세요."),

    ;

    private final HttpStatus httpStatus;

    private final String errorCode;

    private final String errorMessage;
}
