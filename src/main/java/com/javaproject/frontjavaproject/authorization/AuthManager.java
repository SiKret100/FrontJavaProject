package com.javaproject.frontjavaproject.authorization;

public class AuthManager {

    private static String token;

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token;
    }
}
