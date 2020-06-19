package com.victory.blog.security.jwt;

import java.io.Serializable;

public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -3197295944359302166L;

    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
