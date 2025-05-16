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

        T value = (T) Optional.ofNullable(exchange.getAttribute(key.getName()))
                .orElseThrow(() -> new GatewayException(GatewayErrorCode.NULL_ATTRIBUTE_ERROR));

        log.debug("[Exchange] [Method: GET] [Key: {}] [Value: {}]", key.getName(), value);
        return value;
    }

    /**
     * Exchange의 Attribute 설정
     */
    public static void setAttribute(ServerWebExchange exchange, Key key, Object value) {

        log.debug("[Exchange] [Method: SET] [Key: {}] [Value: {}]", key.getName(), value);
        exchange.getAttributes().put(key.getName(), value);
    }

    public static String getRequestHeader(ServerWebExchange exchange, Key key) {

        String value = Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(key.getName()))
                .orElseThrow(() -> new GatewayException(GatewayErrorCode.NULL_HEADER_ERROR));

        log.debug("[RequestHeader] [Method: GET] [Key: {}] [Value: {}]", key.getName(), value);
        return value;
    }

    public static ServerWebExchange setRequestHeader(ServerWebExchange exchange, Key key, String value) {

        log.debug("[RequestHeader] [Method: SET] [Key: {}] [Value: {}]", key.getName(), value);
        return exchange.mutate().request(exchange.getRequest().mutate().header(key.getName(), value).build()).build();
    }
}
