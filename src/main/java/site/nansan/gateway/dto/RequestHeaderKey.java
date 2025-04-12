package site.nansan.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@AllArgsConstructor
public enum RequestHeaderKey {

    // Standard Header
    AUTHORIZATION(HttpHeaders.AUTHORIZATION, String.class),

    USER_AGENT(HttpHeaders.USER_AGENT, String.class),

    // Custom Header
    CHILD_ID("X-Child-Id", Long.class),

    ;

    @Getter
    private final String name;

    private final Class<?> type;

    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() { return (Class<T>) type; }
}
