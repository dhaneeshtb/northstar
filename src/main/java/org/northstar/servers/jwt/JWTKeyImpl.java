package org.northstar.servers.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.northstar.servers.utils.Constants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class JWTKeyImpl implements JWTParser {
    private JWTVerifier verifier = null;
    private Algorithm algorithm;
    private String issuer;

    public JWTKeyImpl(String issuer, Algorithm algorithm) {
        this.algorithm = algorithm;
        this.issuer = issuer;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    private JWTVerifier getVerifier() throws SecurityException {
        if (verifier == null) {
            Verification verification = JWT.require(getAlgorithm());
            try {
                if (issuer != null && !issuer.isEmpty()) {
                    verification = verification.withIssuer(issuer);
                }
                verifier = verification.build();
            } catch (Exception e) {
                throw new SecurityException(e);
            }
        }
        return verifier;
    }

    public String createToken(AuthRequest.AuthInfo user) throws SecurityException {
        try {
            Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            Instant expiration = issuedAt.plus(1, ChronoUnit.DAYS);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(user.getUsername())
                    .withClaim("role", user.getRole())
                    .withClaim("user", Constants.OBJECT_MAPPER.convertValue(user, Map.class))
                    .withExpiresAt(expiration)
                    .withIssuedAt(issuedAt)
                    .sign(getAlgorithm());
        } catch (Exception exception) {
            throw new SecurityException(exception);
        }
    }

    public AuthRequest.AuthInfo verify(String token) {
        DecodedJWT decodedJWT;
        decodedJWT = getVerifier().verify(token);
        AuthRequest.AuthInfo authInfo = new AuthRequest.AuthInfo();
        if (decodedJWT.getClaim("role") != null) {
            authInfo.setRole(decodedJWT.getClaim("role").asString());
        }
        authInfo.setUsername(decodedJWT.getSubject());
        return authInfo;

    }
}
