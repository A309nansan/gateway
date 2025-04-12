package site.nansan.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;
import site.nansan.gateway.dto.ExchangeKey;
import site.nansan.gateway.util.ExchangeUtil;

@Slf4j
@Component
public class AuthenticationCheckFilter implements GlobalFilter, Ordered {

    private static final String PUBLIC_PREFIX_REGEX = "^/api/v\\d+/[\\w-]+/public/.*";
    private static final String PRIVATE_PREFIX_REGEX = "^/api/v\\d+/[\\w-]+/private/.*";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String requestPath = exchange.getRequest().getURI().getPath();

        if (isPublicPath(requestPath)) {

            log.debug("Public Url 요청: {}", requestPath);

            ExchangeUtil.setValue(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.FALSE);
            return chain.filter(exchange);
        }

        if (isPrivatePath(requestPath)) {

            log.debug("Private Url 요청: {}", requestPath);

            ExchangeUtil.setValue(exchange, ExchangeKey.IS_AUTH_REQUIRED, Boolean.TRUE);
            return chain.filter(exchange);
        }

        log.warn("인증 여부를 판단할 수 없는 경로 요청: {}", requestPath);
        throw new GatewayException(GatewayErrorCode.URL_INVALID_FORMAT);
    }

    private boolean isPublicPath(String path) {

        return path.matches(PUBLIC_PREFIX_REGEX);
    }

    private boolean isPrivatePath(String path) {

        return path.matches(PRIVATE_PREFIX_REGEX);
    }

    @Override
    public int getOrder() {

        return 0;
    }
}
