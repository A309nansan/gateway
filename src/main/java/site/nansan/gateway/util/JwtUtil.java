package site.nansan.gateway.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import site.nansan.gateway.dto.Key;
import site.nansan.gateway.dto.impl.GatewayErrorCode;
import site.nansan.gateway.exception.GatewayException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JwtUtil {

    private final SecretKey JWT_SECRET_KEY;

    public JwtUtil(
            @Value("${token.jwt.secret}") String secretKey
    ) {

        JWT_SECRET_KEY = new SecretKeySpec(secretKey.getBytes(
                StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    // JWT 검증
    public void validateToken(String token) {

        try {
            Jwts.parser().verifyWith(JWT_SECRET_KEY).build()
                    .parseSignedClaims(token);
        } catch (RuntimeException e) {
            throw new GatewayException(toGatewayErrorCode(e));
        }
    }

    public <T> T getClaimValue(String token, Key key) {

        try {
            return Jwts.parser().verifyWith(JWT_SECRET_KEY).build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get(key.getName(), key.getType());
        } catch (RuntimeException e) {
            throw new GatewayException(toGatewayErrorCode(e));
        }
    }

    private GatewayErrorCode toGatewayErrorCode(RuntimeException e) {

        if (e instanceof SecurityException || e instanceof MalformedJwtException) {
            return GatewayErrorCode.JWT_VALIDATION_FAILED;
        } else if (e instanceof ExpiredJwtException) {
            return GatewayErrorCode.JWT_EXPIRED;
        } else if (e instanceof UnsupportedJwtException) {
            return GatewayErrorCode.JWT_UNSUPPORTED;
        } else if (e instanceof IllegalArgumentException) {
            return GatewayErrorCode.JWT_CLAIMS_EMPTY;
        }

        return GatewayErrorCode.JWT_UNKNOWN_ERROR;
    }
}
