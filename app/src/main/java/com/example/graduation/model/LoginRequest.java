package com.example.graduation.model;


public class LoginRequest {
    String id;
    String password;

    public LoginRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public void setUsername(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


