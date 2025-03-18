package site.nansan.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.nansan.gateway.util.JWTUtil;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements GlobalFilter {

    private final JWTUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 요청 URI 검사 (예외 처리할 엔드포인트)
        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        // Authorization 헤더에서 JWT 추출
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (token == null || token.isEmpty()) {
            return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }

        // "Bearer " 접두사 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // JWT 유효성 검사
        if (jwtUtil.isExpired(token)) {
            return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
        }

        if (!"access".equals(jwtUtil.getCategory(token))) {
            return onError(exchange, "Invalid access token", HttpStatus.UNAUTHORIZED);
        }

        // 유저 정보 추출
        Long userId = jwtUtil.getUserId(token);
        String nickname = jwtUtil.getNickName(token);
        String role = jwtUtil.getRole(token);
        =

        if (userId == null || nickname == null || role == null) {
            return onError(exchange, "Invalid token payload", HttpStatus.UNAUTHORIZED);
        }

        // 요청 헤더에 사용자 정보 추가 (라우트된 서비스에서 사용 가능)
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Nickname", nickname)
                .header("X-User-Role", role)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /** 특정 경로는 필터를 거치지 않도록 설계 */
    private boolean isExcludedPath(String path) {
        return path.matches("^/api/v\\d+/auth/login$") ||  // 정확한 로그인 경로 추가
                path.matches("^/api/v\\d+/auth/token$") ||
                path.matches("^/api/v\\d+/auth/reissue$") ||
                path.matches("^/api/v\\d+/auth/logout$") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/v3/api-docs.yaml") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/swagger-ui/index.html");
    }

    /** 에러 응답 처리 */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        log.error("JWT 검증 실패: {}", message);
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        return response.writeWith(Mono.just(buffer));
    }

}