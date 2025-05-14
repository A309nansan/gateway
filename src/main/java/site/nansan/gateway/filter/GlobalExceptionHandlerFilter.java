package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.impl.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;
import site.nansan.gateway.exception.ExceptionHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionHandlerFilter implements GlobalFilter, Ordered {

    private final ExceptionHandler exceptionHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange).onErrorResume(exception  -> {

            log.error("[Runtime Exception 발생] {}", exception.getMessage(), exception);

            if (exception instanceof GatewayException gatewayException) {
                return exceptionHandler.writeError(exchange, gatewayException.getErrorCode());
            }

            return exceptionHandler.writeError(exchange, GatewayErrorCode.INTERNAL_SERVER_ERROR);
        });
    }

    @Override
    public int getOrder() {

        return -1;
    }
}
