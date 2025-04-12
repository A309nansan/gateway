package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.ExchangeKey;
import site.nansan.gateway.dto.RequestHeaderKey;
import site.nansan.gateway.service.PassportService;
import site.nansan.gateway.util.ExchangeUtil;
import site.nansan.gateway.util.RequestHeaderUtil;


@Slf4j
@Component
@RequiredArgsConstructor
public class PassportFilter implements GlobalFilter, Ordered {

    private final PassportService passportService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Long userId = ExchangeUtil.getValue(exchange, ExchangeKey.USER_ID);
        Long childId = RequestHeaderUtil.getValue(exchange, RequestHeaderKey.CHILD_ID);
        String userAgent = RequestHeaderUtil.getValue(exchange, RequestHeaderKey.USER_AGENT);

        log.debug("[User Id : {}], [Child Id : {}], [User Agent : {}]", userId, childId, userAgent);

        Mono<String> certificatedPassport = passportService.getCertificatedPassport(userId, childId, userAgent);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {

        return 2;
    }
}
