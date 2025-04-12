package site.nansan.gateway.dto;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();

    String getErrorCode();

    String getErrorMessage();
}
