package site.nansan.gateway.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.nansan.gateway.dto.Key;

@AllArgsConstructor
public enum ExchangeKey implements Key {

    IS_AUTH_REQUIRED("isAuthRequired", Boolean.class),

    USER_ID("user_id", Long.class),
    EMAIL("email", String.class),
    NICKNAME("nickname", String.class),
    ROLE("role", String.class),
    CHILD_ID("child_id", Long.class),

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
