package site.nansan.gateway.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import site.nansan.gateway.dto.DeviceKey;
import site.nansan.gateway.dto.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;
import site.nansan.gateway.util.DeviceUtil;
import site.nansan.passport.PassportOuterClass;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassportService {

    private final WebClient userServiceWebClient;

    public Mono<String> getCertificatedPassport(Long userId, Long childId, String userAgent) {

        String uri = UriComponentsBuilder
                .fromPath("/users/internal/{userId}")
                .queryParam("childId", childId)
                .build(userId)
                .toString();

        log.debug("[요청 URL : {}]", uri);

        return userServiceWebClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {

                    JsonNode root;

                    try {
                        root = new ObjectMapper().readTree(json);
                    } catch (Exception e) {
                        throw new GatewayException(GatewayErrorCode.JSON_PROCESS_ERROR);
                    }
                    PassportOuterClass.User user = buildUser(root.get("user"));
                    PassportOuterClass.Child child = buildChild(root.get("child"));
                    PassportOuterClass.Device device = buildDevice(userAgent);

                    // Passport 조립 (hmac 제외)
                    PassportOuterClass.Passport.Builder builder = PassportOuterClass.Passport.newBuilder();
                    builder = buildPassport(builder, user, child, device);

                    byte[] raw = builder.clone().clearHmac().build().toByteArray();
                    String hmac = PassportUtil.generateHmac(hmacSecret, raw);

                    builder.setHmac(hmac);
                    return Base64.getEncoder().encodeToString(builder.build().toByteArray());
                });
    }

    private PassportOuterClass.User buildUser(JsonNode userNode) {

        return PassportOuterClass.User.newBuilder()
                .setId(userNode.get("id").asLong())
                .setPlatformId(userNode.get("platformId").asText())
                .setSocialPlatform(userNode.get("socialPlatform").asText())
                .setEmail(userNode.get("email").asText())
                .setNickName(userNode.get("nickName").asText())
                .setRole(userNode.get("role").asText())
                .setDetailStatus(userNode.get("detailStatus").asBoolean())
                .setHashId(userNode.get("hashId").asText())
                .setProfileImageUrl(userNode.get("profileImageUrl").asText())
                .build();
    }

    private PassportOuterClass.Child buildChild(JsonNode childNode) {

        if (childNode == null || childNode.isNull())
            return null;

        return PassportOuterClass.Child.newBuilder()
                .setId(childNode.get("id").asLong())
                .setName(childNode.get("name").asText())
                .setProfileImageUrl(childNode.get("profileImageUrl").asText())
                .setBirthDate(childNode.get("birthDate").asText())
                .setGrade(childNode.get("grade").asText())
                .setGender(childNode.get("gender").asText())
                .build();
    }

    private PassportOuterClass.Device buildDevice(String userAgent) {

        Map<DeviceKey, String> deviceMap = DeviceUtil.parse(userAgent);

        return PassportOuterClass.Device.newBuilder()
                .setAppName(deviceMap.get(DeviceKey.APP_NAME))
                .setAppVersion(deviceMap.get(DeviceKey.APP_VERSION))
                .setOs(deviceMap.get(DeviceKey.OS))
                .setDevice(deviceMap.get(DeviceKey.DEVICE))
                .setModel(deviceMap.get(DeviceKey.MODEL))
                .setLocale(deviceMap.get(DeviceKey.LOCALE))
                .setNetwork(deviceMap.get(DeviceKey.NETWORK))
                .build();
    }

    private PassportOuterClass.Passport.Builder buildPassport(
            PassportOuterClass.Passport.Builder builder,
            PassportOuterClass.User user,
            PassportOuterClass.Child child,
            PassportOuterClass.Device device
    ) {

        if (child != null) {
            builder.setChild(child);
        }
        return builder.setUser(user).setDevice(device);
    }
}
