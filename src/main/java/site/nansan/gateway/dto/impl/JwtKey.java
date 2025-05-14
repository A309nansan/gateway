package site.nansan.gateway.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.nansan.gateway.dto.Key;

@AllArgsConstructor
public enum JwtKey implements Key {

    ID("id", Long.class),
    SOCIAL_PLATFORM("social_platform", String.class),
    EMAIL("email", String.class),
    NICKNAME("nickname", String.class),
    ROLE("role", String.class),

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
