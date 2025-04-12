package site.nansan.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.nansan.gateway.exception.GatewayException;

@AllArgsConstructor
public enum DeviceKey {

    APP_NAME("appName"),
    APP_VERSION("appVersion"),
    OS("os"),
    DEVICE("device"),
    MODEL("model"),
    LOCALE("locale"),
    NETWORK("network"),

    ;

    @Getter
    private final String name;

    private static final DeviceKey[] DEVICE_KEYS = values();

    public static DeviceKey deviceKeyAt(int index) {

        if (index < 0 || index >= DEVICE_KEYS.length) {
            throw new GatewayException(GatewayErrorCode.ILLEGAL_INDEX_ERROR);
        }

        return DEVICE_KEYS[index];
    }
}
