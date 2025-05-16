package site.nansan.gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import site.nansan.gateway.dto.Key;
import site.nansan.gateway.dto.impl.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class PassportUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${token.passport.secret}")
    private String PASSPORT_SECRET_KEY;

    public void setKeyValue(Map<String, Object> map, Key key, Object value) {

        log.debug("[Passport] [Method: SET] [Key: {}] [Value: {}]", key.getName(), value);
        map.put(key.getName(), value);
    }

    public String buildHmacToken(Map<String, Object> values) {

        try {
            String json = buildMapToJson(values);
            String payloadB64 = convertB64(json);
            byte[] signedBytes = signedTokenByHMAC(payloadB64);
            String signedB64 = convertB64(signedBytes);

            return putTogether(payloadB64, signedB64);

        } catch (JsonProcessingException e) {
            throw new GatewayException(GatewayErrorCode.JSON_CONVERT_ERROR);
        } catch (NoSuchAlgorithmException e) {
            throw new GatewayException(GatewayErrorCode.NO_ALGORITHM_ERROR);
        } catch (InvalidKeyException e) {
            throw new GatewayException(GatewayErrorCode.INVALID_KEY_ERROR);
        }
    }

    /**
     * @param map : 사용자 정보가 (key, value) 쌍으로 담긴 Map
     * @return Map의 (key, value) 쌍을 Json 형태로 변환한 문자열
     */
    private String buildMapToJson(Map<String, Object> map) throws JsonProcessingException {

        String jsonString = MAPPER.writeValueAsString(map);

        log.debug("[Passport] [Method: Build Json String] [Value: {}]", jsonString);
        return jsonString;
    }

    /**
     * @param jsonString : Map의 (key, value) 쌍을 Json 형태로 변환한 문자열
     * @return Json 형태의 문자열을 Base64를 이용해 인코딩한 문자열
     */
    private String convertB64(String jsonString) {

        String jsonB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonString.getBytes());

        log.debug("[Passport] [Method: Build String with Base64 from Json] [Value: {}]", jsonB64);
        return jsonB64;
    }

    /**
     * @param jsonByteArray : Map의 (key, value) 쌍을 Json 형태로 변환한 Byte 배열
     * @return Json 형태의 문자열을 Base64를 이용해 인코딩한 문자열
     */
    private String convertB64(byte[] jsonByteArray) {

        String jsonB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(jsonByteArray);

        log.debug("[Passport] [Method: Build String with Base64 from Byte Array] [Value: {}]", jsonB64);
        return jsonB64;
    }

    /**
     * @param jsonStringB64 : Json 형태의 문자열을 Base64를 이용해 인코딩한 문자열
     * @return 비밀키와 HMAC 알고리즘을 활용해 서명한 token의 byte 배열
     */
    private byte[] signedTokenByHMAC(String jsonStringB64) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(PASSPORT_SECRET_KEY.getBytes(), "HmacSHA256"));
        byte[] signBytes = mac.doFinal(jsonStringB64.getBytes());

        log.debug("[Passport] [Method: Sign Token By HMAC] [Value: {}]", signBytes);
        return signBytes;
    }

    private String putTogether(String payloadB64, String signedB64) {

        String token = payloadB64 + "." + signedB64;

        log.debug("[Passport] [Method: Make Token] [Value: {}]", token);
        return token;
    }
}
