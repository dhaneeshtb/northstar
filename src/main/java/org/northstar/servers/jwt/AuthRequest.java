package org.northstar.servers.jwt;

import com.fasterxml.jackson.databind.JsonNode;

public class AuthRequest {
    private AuthRequest(){

    }
    public static class LoginResponse{
        private String token;

        private String appTargetURL;

        private boolean status;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getAppTargetURL() {
            return appTargetURL;
        }

        public void setAppTargetURL(String appTargetURL) {
            this.appTargetURL = appTargetURL;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        private User user;

        public LoginResponse(String token,User user){
            this.token=token;
            this.user=user;
            if(token!=null){
                this.status=true;
            }
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class AuthInfo{
        private String username;
        private String role;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class User{

        private String username;

        private String profileImage;

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        private String password;

        private String role;

        private String name;

        public JsonNode getAttributes() {
            return attributes;
        }

        public void setAttributes(JsonNode attributes) {
            this.attributes = attributes;
        }

        private JsonNode attributes;


        public String getName() {
            return name;
        }



        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        private String email;

        private String phone;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public long getMts() {
            return mts;
        }

        public void setMts(long mts) {
            this.mts = mts;
        }

        public long getCts() {
            return cts;
        }

        public void setCts(long cts) {
            this.cts = cts;
        }

        private long mts;

        private long cts;
    }


    public static class LoginInput{
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        private String password;

        private long attemptTimestamp;

        public long getAttemptTimestamp() {
            return attemptTimestamp;
        }

        public void setAttemptTimestamp(long attemptTimestamp) {
            this.attemptTimestamp = attemptTimestamp;
        }
    }




}
