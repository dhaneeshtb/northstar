package org.northstar.servers.auth;

import org.northstar.servers.jwt.AuthRequest;

public interface UserStore {

    public AuthRequest.User getUser(String username);

}
