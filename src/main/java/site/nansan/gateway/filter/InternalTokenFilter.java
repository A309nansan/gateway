package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.impl.ExchangeKey;
import site.nansan.gateway.dto.impl.PassportKey;
import site.nansan.gateway.dto.impl.RequestHeaderKey;
import site.nansan.gateway.util.ExchangeUtil;
import site.nansan.gateway.util.PassportUtil;

import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class InternalTokenFilter implements GlobalFilter, Ordered {

    private final PassportUtil passportUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Long userId = ExchangeUtil.getAttribute(exchange, ExchangeKey.USER_ID);
        String email = ExchangeUtil.getAttribute(exchange, ExchangeKey.EMAIL);
        String nickname = ExchangeUtil.getAttribute(exchange, ExchangeKey.NICKNAME);
        String role = ExchangeUtil.getAttribute(exchange, ExchangeKey.ROLE);
        Long childId = ExchangeUtil.getAttribute(exchange, ExchangeKey.CHILD_ID);

        // 순서를 보장하는 LinkedHashMap
        Map<String,Object> values = new LinkedHashMap<>();

        passportUtil.setKeyValue(values, PassportKey.USER_ID, userId);
        passportUtil.setKeyValue(values, PassportKey.EMAIL, email);
        passportUtil.setKeyValue(values, PassportKey.NICKNAME, nickname);
        passportUtil.setKeyValue(values, PassportKey.ROLE, role);
        passportUtil.setKeyValue(values, PassportKey.CHILD_ID, childId);

        // 토큰 생성
        String token = passportUtil.buildHmacToken(values);

        // 헤더에 Token이 추가된 새로운 Exchange 생성
        ServerWebExchange exchangeWithToken = ExchangeUtil.setRequestHeader(exchange, RequestHeaderKey.PASSPORT, token);

        return chain.filter(exchangeWithToken);
    }

    @Override
    public int getOrder() {

        return 2;
    }
}
