package org.northstar.servers.jwt;

import com.auth0.jwt.algorithms.Algorithm;

public interface JWTParser {
    Algorithm getAlgorithm();
    AuthRequest.AuthInfo verify(String token);
    String createToken(AuthRequest.AuthInfo user);
    String createToken(AuthRequest.User user);

}
