package com.ticketing.backend.util;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

public class KeyGenerator {
    public static void main(String[] args) {
        String key = Encoders.BASE64.encode(
            Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded()
        );
        System.out.println(key);
    }
}