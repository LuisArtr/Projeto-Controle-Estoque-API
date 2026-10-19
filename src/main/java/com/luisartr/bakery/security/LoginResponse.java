package com.luisartr.bakery.security;

public record LoginResponse(String token, long expiresInSeconds, String role) {}