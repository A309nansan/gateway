package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.GatewayErrorCode;
import site.nansan.gateway.dto.JwtKey;
import site.nansan.gateway.dto.RequestHeaderKey;
import site.nansan.gateway.exception.GatewayException;
import site.nansan.gateway.dto.ExchangeKey;
import site.nansan.gateway.util.ExchangeUtil;
import site.nansan.gateway.util.JwtUtil;
import site.nansan.gateway.util.RequestHeaderUtil;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        Boolean isAuthRequired = ExchangeUtil.getValue(exchange, ExchangeKey.IS_AUTH_REQUIRED);

        if (Boolean.FALSE.equals(isAuthRequired)) {
            return chain.filter(exchange);
        }

        // JWT 존재 여부 확인
        String jwtWithBearer = (String) Optional.ofNullable(
                RequestHeaderUtil.getValue(exchange, RequestHeaderKey.AUTHORIZATION)).orElseThrow(
                        () -> new GatewayException(GatewayErrorCode.JWT_VALIDATION_FAILED, "JWT가 존재하지 않습니다.")
                );

        // JWT 형식이 올바른지 확인
        if (!jwtWithBearer.startsWith(BEARER_PREFIX)) {
            throw new GatewayException(GatewayErrorCode.JWT_VALIDATION_FAILED, "JWT 형식이 올바르지 않습니다.");
        }

        // JWT 부분만 추출
        String accessToken = jwtWithBearer.substring("Bearer ".length());

        // JWT 유효성 검증: Error 내부에서 처리
        jwtUtil.validateToken(accessToken);

        log.debug("JWT 유효성 검증 성공");

        // JWT Claim 반환: Error 내부에서 처리
        Long userId = jwtUtil.getClaimValue(accessToken, JwtKey.USER_ID);
        ExchangeUtil.setValue(exchange, ExchangeKey.USER_ID, userId);

        log.debug("요청받은 User Id: {}", userId);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {

        return 1;
    }
}
