package site.nansan.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandler {

    private final ObjectMapper objectMapper;

    public Mono<Void> writeError(
            ServerWebExchange exchange,
            ErrorCode errorCode
    ) {

        return writeJson(exchange, errorCode.getHttpStatus(), ErrorResponse.from(errorCode));
    }

    public Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus httpStatus,
            String errorMessage
    ) {

        return writeJson(exchange, httpStatus, ErrorResponse.from("GATEWAY_5XX_1", errorMessage));
    }

    public Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus httpStatus,
            String errorCode,
            String errorMessage
    ) {

        return writeJson(exchange, httpStatus, ErrorResponse.from(errorCode, errorMessage));
    }

    private Mono<Void> writeJson(
            ServerWebExchange exchange,
            HttpStatus httpStatus,
            ErrorResponse errorResponse
    ) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            log.error("Failed to serialize error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}
