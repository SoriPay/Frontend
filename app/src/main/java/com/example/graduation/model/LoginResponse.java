package com.example.graduation.model;

public class LoginResponse {
    boolean success;
    String message;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


