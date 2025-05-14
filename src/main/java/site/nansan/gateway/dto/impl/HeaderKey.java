package site.nansan.gateway.dto.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.nansan.gateway.dto.Key;

@AllArgsConstructor
public enum HeaderKey implements Key {


    USER_AGENT("User-Agent", String.class),
    SOURCE("source", String.class),

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
