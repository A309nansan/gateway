package site.nansan.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ExchangeKey {

    IS_AUTH_REQUIRED("isAuthRequired", Boolean.class),
    USER_ID("userId", Long.class),
    CHILD_ID("childId", Long.class),

    ;

    @Getter
    private final String name;

    private final Class<?> type;

    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() { return (Class<T>) type; }
}
