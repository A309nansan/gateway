package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.Key;
import site.nansan.gateway.dto.impl.ExchangeKey;
import site.nansan.gateway.dto.impl.RequestHeaderKey;
import site.nansan.gateway.util.ExchangeUtil;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class PassportFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Long userId = ExchangeUtil.getAttribute(exchange, ExchangeKey.USER_ID);
        Long childId = ExchangeUtil.getRequestHeader(exchange, RequestHeaderKey.CHILD_ID);
        String userAgent = ExchangeUtil.getRequestHeader(exchange, RequestHeaderKey.USER_AGENT);

        log.debug("[User Id : {}], [Child Id : {}], [User Agent : {}]", userId, childId, userAgent);

        Map<Key, String> map = DeviceUtil.parseUserAgent(userAgent);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {

        return 2;
    }
}
