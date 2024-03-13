package org.northstar.servers.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.northstar.servers.jwt.AuthRequest;
import org.northstar.servers.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class UserFileStore implements UserStore{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFileStore.class);

    private Map<String, AuthRequest.User> userMap = new HashMap<>();

    public UserFileStore(String fileJsonPath){
        configureAdminUsersFile(fileJsonPath);
    }
    private void configureAdminUsersFile(String usersFile) {
        try {
            if (usersFile != null && new File(usersFile).exists()) {
                JsonNode node = Constants.OBJECT_MAPPER.readTree(new FileReader(usersFile));
                node.get("users").forEach(profile -> {
                    String role = profile.has("role") ? profile.get("role").asText():"user";
                    String username = profile.get("username").asText();
                    String password = profile.get("password").asText();
                    String name = profile.has("name") ? profile.get("name").asText() : username;
                    try {
                        AuthRequest.User user = buildUserObject(username, password, name, "", "", role);
                        userMap.put(user.getUsername(),user);
                    } catch (Exception e) {
                        LOGGER.info("vanguard user configuration failed for  {}" , username);
                    }
                });
            }
        }catch (Exception e){
            LOGGER.info("users configuration failed",e);
        }
    }
    private AuthRequest.User buildUserObject(String username,String password,String name,String email,String phone,String role) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        AuthRequest.User user = new AuthRequest.User();
        user.setUsername(username);
        user.setPassword(AuthUtils.onewayHash(password));
        user.setRole(role);
        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        return user;
    }
    @Override
    public AuthRequest.User getUser(String username) {
        return userMap.get(username);
    }
}
