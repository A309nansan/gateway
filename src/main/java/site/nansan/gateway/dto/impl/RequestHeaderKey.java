package site.nansan.gateway.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import site.nansan.gateway.dto.Key;

@AllArgsConstructor
public enum RequestHeaderKey implements Key {

    // Standard Header
    AUTHORIZATION(HttpHeaders.AUTHORIZATION, String.class),
    USER_AGENT(HttpHeaders.USER_AGENT, String.class),

    // Custom Header
    CHILD_ID("X-Child-Id", Long.class),
    SOURCE("X-Source", String.class),
    PASSPORT("X-Passport", String.class),
    ;

    @Getter
    private final String name;

    private final Class<?> type;

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getType() {

        return (Class<T>) type;
    }
}
