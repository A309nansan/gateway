package site.nansan.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.impl.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;
import site.nansan.gateway.dto.impl.ExchangeKey;
import site.nansan.gateway.util.ExchangeUtil;

@Slf4j
@Component
public class AuthenticationCheckFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestPath = exchange.getRequest().getURI().getPath();

        if (isPrivatePath(requestPath)) {

            log.debug("Private Url 요청: {}", requestPath);
            ExchangeUtil.setAttribute(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.TRUE);
            return chain.filter(exchange);
        }

        if (isPublicPath(requestPath)) {

            log.debug("Public Url 요청: {}", requestPath);
            ExchangeUtil.setAttribute(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.FALSE);
            return chain.filter(exchange);
        }

        if (isSwaggerPath(requestPath)) {

            log.debug("Swagger Url 요청: {}", requestPath);
            ExchangeUtil.setAttribute(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.FALSE);
            return chain.filter(exchange);
        }

        if (isDetourPath(requestPath)) {

            log.debug("Detour Url 요청: {}", requestPath);
            ExchangeUtil.setAttribute(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.FALSE);
            return chain.filter(exchange);
        }

        log.warn("잘못된 형식의 경로 요청: {}", requestPath);
        throw new GatewayException(GatewayErrorCode.URL_INVALID_FORMAT);
    }

    private boolean isPrivatePath(String path) {

        return path.matches("^/api/v\\d+/private/.*");
    }

    private boolean isPublicPath(String path) {

        return path.matches("^/api/v\\d+/public/.*");
    }

    private boolean isSwaggerPath(String path) {

        return path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/v3/api-docs(?:\\.yaml)?$") ||    // JSON & YAML
                path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/swagger-ui(?:/.*)?$") ||        // swagger-ui 및 하위 경로
                path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/swagger-resources(?:/.*)?$");   // swagger-resources 및 하위 경로
    }

    private boolean isDetourPath(String path) {

        return path.matches("^/api/v\\d+/handwrite(?:/.*)?$"); // handwrite 서비스는 비인증 서비스
    }

    @Override
    public int getOrder() {

        return 0;
    }
}
