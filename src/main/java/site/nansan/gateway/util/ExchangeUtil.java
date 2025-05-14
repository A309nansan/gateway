package site.nansan.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import site.nansan.gateway.dto.Key;
import site.nansan.gateway.dto.impl.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeUtil {

    /**
     * Exchange의 Attribute 반환
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(ServerWebExchange exchange, Key key) {

        log.debug("[Exchange] [Method : GET] [Key: {}]", key.getName());

        return (T) Optional.ofNullable(exchange.getAttribute(key.getName()))
                .orElseThrow(() -> new GatewayException(GatewayErrorCode.NULL_SERVER_ERROR));
    }

    /**
     * Exchange의 Attribute 설정
     */
    public static void setAttribute(ServerWebExchange exchange, Key key, Object value) {

        log.debug("[Exchange] [Method : SET] [Key: {}] [Value: {}]", key.getName(), value);

        exchange.getAttributes().put(key.getName(), value);
    }

    /**
     * Request Header의 Key 반환
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequestHeader(ServerWebExchange exchange, Key key) {

        log.debug("[Exchange] [Method : getValue()] [Key: {}]", key.getName());

        return (T) exchange.getRequest().getHeaders().getFirst(key.getName());
    }

    /**
     * Request Header의 Key 설정
     */
    public static ServerWebExchange setRequestHeader(ServerWebExchange exchange, Key key, String value) {

        log.debug("[Exchange] [Method : setValue()] [Key: {}] [Value: {}]", key.getName(), value);

        return exchange.mutate()
                .request(builder -> builder.header(key.getName(), value))
                .build();
    }
}
