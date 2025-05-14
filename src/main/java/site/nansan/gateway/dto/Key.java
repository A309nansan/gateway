package site.nansan.gateway.dto;

public interface Key {

    String getName();

    <T> Class<T> getType();
}
