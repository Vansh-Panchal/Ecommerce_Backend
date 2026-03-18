package com.example.demo.response;

public class AuthResponse {

    private String jwt;
    private String role;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String jwt, String role, String message) {
        this.jwt = jwt;
        this.role = role;
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}