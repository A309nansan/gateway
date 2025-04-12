package site.nansan.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtToPassportFilter implements GlobalFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String PASSPORT_HEADER = "X-Passport";

    private final WebClient userServiceWebClient;

    public JwtToPassportFilter(
            @Qualifier("userServiceWebClient") WebClient userServiceWebClient)
    {

        this.userServiceWebClient = userServiceWebClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1) Request Header에서 JWT 추출
        ServerHttpRequest request = exchange.getRequest();
        String jwtToken = request.getHeaders().getFirst(AUTH_HEADER);

        // JWT가 없는 경우 다음 Filter 호출
        if (jwtToken == null || jwtToken.isEmpty()) {

            return chain.filter(exchange);
        }

        // 2) JWT 유효성 검사 + user_id 추출

        String path = request.getURI().getPath();

        // 디버깅 로그 추가
        log.info("🔍 요청 URI: {}", path);

        // 요청 URI 검사 (예외 처리할 엔드포인트)
        if (isExcludedPath(path) || isSwaggerRequest(request)) {
            log.info("✅ 필터 예외 경로 or Swagger 요청 경로: {}", path);  // 추가된 로그
            return chain.filter(exchange);
        } else {
            log.info("⛔ 필터 적용됨: {}", path);  // 추가된 로그
        }

        // 대신, JWT를 제거하기 위해 Authorization 헤더를 삭제합니다.
        ServerHttpRequest modifiedRequest = request.mutate()
                .headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /** 특정 경로는 필터를 거치지 않도록 설계 */
    private boolean isExcludedPath(String path) {
        return path.matches("^/api/v\\d+/user/login$") ||
                path.matches("^/api/v\\d+/user/logout$") ||
                path.matches("^/api/v\\d+/user/reissue$") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/v3/api-docs.yaml") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/swagger-ui/index.html") ||
                path.startsWith("/user/swagger-resources") ||
                path.startsWith("/user/swagger-ui.html") ||
                path.startsWith("/user/swagger-ui/index.html") ||
                path.startsWith("/user/swagger-ui") ||
                path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/v3/api-docs(?:\\.yaml)?$") ||   // JSON & YAML
                path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/swagger-ui(?:/.*)?$") ||        // swagger-ui 및 하위 경로
                path.matches("^/api/v\\d+/[a-zA-Z0-9-]+/swagger-resources(?:/.*)?$");   // swagger-resources 및 하위 경로
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

    /**
     * Swagger 요청 여부 확인 메서드
     * Swagger UI에서 요청할 경우 Referer나 User-Agent에 "swagger-ui" 또는 "Swagger"가 포함
     */
    private boolean isSwaggerRequest(ServerHttpRequest request) {

        String referer = request.getHeaders().getFirst("referer");
        String userAgent = request.getHeaders().getFirst("user-agent");

        return (referer != null && referer.contains("swagger-ui")) ||
                (userAgent != null && userAgent.toLowerCase().contains("swagger"));
    }
}
