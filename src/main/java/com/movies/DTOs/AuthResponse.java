package com.movies.DTOs;

public class AuthResponse {
    private String accessToken;

    // Constructors
    public AuthResponse() {
    }

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Getter and Setter
    // accessToken
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}