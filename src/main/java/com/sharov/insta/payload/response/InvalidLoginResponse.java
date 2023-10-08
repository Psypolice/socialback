package com.sharov.insta.payload.response;

import lombok.Getter;

@Getter
public class InvalidLoginResponse {

    private final String message;

    public  InvalidLoginResponse() {
        this.message = "Invalid username or password";
    }
}
