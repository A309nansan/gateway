package site.nansan.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.nansan.gateway.dto.DeviceKey;
import site.nansan.gateway.dto.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceUtil {

    /**
     * @param userAgent : "{App Name}/{App Version} (Os={}; Device={}; Model={}; Locale={}; Network={};)"
     */
    public static Map<DeviceKey, String> parse(String userAgent) {

        StringBuilder sb = new StringBuilder();

        int split = userAgent.indexOf('(');
        int len = userAgent.length();

        final int HEADER_START = 0;
        final int HEADER_END = split - 1;
        final int BODY_START = split + 1;
        final int BODY_END = len - 1;

        Map<DeviceKey, String> deviceMap = new HashMap<>();
        int keyOrder = 0;
        boolean readMode = false;

        for (int i = HEADER_START; i < HEADER_END; i++) {

            char letter = charAt(userAgent, i);

            if (letter == '/') {

                addAttribute(deviceMap, DeviceKey.deviceKeyAt(keyOrder++), sb);
                continue;
            }
            sb.append(letter);
        }

        deviceMap.put(DeviceKey.deviceKeyAt(keyOrder++), sb.toString());
        sb.setLength(0);

        for (int i = BODY_START; i < BODY_END; i++) {

            char letter = charAt(userAgent, i);

            if (letter == '=') {
                readMode = true;
                continue;
            }

            if (letter == ';') {

                addAttribute(deviceMap, DeviceKey.deviceKeyAt(keyOrder++), sb);
                readMode = false;
                continue;
            }

            if (readMode) {
                sb.append(letter);
            }
        }

        // Key 갯수가 부족하거나, 포맷이 틀린 경우의 Error 처리 추가 예정

        return Collections.unmodifiableMap(deviceMap);
    }

    private static void addAttribute(Map<DeviceKey, String> map, DeviceKey deviceKey, StringBuilder sb) {

        String value = sb.toString();

        log.debug("[Device Key : {}], [Value : {}]", deviceKey.getName(), value);

        map.put(deviceKey, value);
        sb.setLength(0);
    }

    private static char charAt(String userAgent, int index) {

        int len = userAgent.length();

        log.debug("[User-Agent], [Length : {}], [index : {}]", len, index);

        if(index < 0 || index >= len) {
            throw new GatewayException(GatewayErrorCode.NULL_SERVER_ERROR);
        }
        return userAgent.charAt(index);
    }
}
