package site.nansan.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import site.nansan.gateway.dto.RequestHeaderKey;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestHeaderUtil {

    @SuppressWarnings("unchecked")
    public static <T> T getValue(ServerWebExchange exchange, RequestHeaderKey key) {

        log.debug("[RequestHeader] [Method : GET] [Key: {}]", key.name());

        return (T) exchange.getRequest().getHeaders().getFirst(key.getName());
    }

    public static ServerWebExchange setValue(ServerWebExchange exchange, RequestHeaderKey key, String value) {

        log.debug("[Exchange] [Method : SET] [Key: {}] [Value: {}]", key.name(), value);

        return exchange.mutate()
                .request(builder -> builder.header(key.getName(), value))
                .build();
    }
}
