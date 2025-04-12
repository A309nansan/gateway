package site.nansan.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import site.nansan.gateway.dto.ExchangeKey;
import site.nansan.gateway.dto.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;

import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getValue(ServerWebExchange exchange, ExchangeKey key) {

        log.debug("[Exchange] [Method : GET] [Key: {}]", key.name());

        return (T) Optional.ofNullable(exchange.getAttribute(key.getName()))
                .orElseThrow(() -> new GatewayException(GatewayErrorCode.NULL_SERVER_ERROR));
    }

    public static void setValue(ServerWebExchange exchange, ExchangeKey key, Object value) {

        log.debug("[Exchange] [Method : SET] [Key: {}] [Value: {}]", key.name(), value);

        exchange.getAttributes().put(key.getName(), value);
    }
}
