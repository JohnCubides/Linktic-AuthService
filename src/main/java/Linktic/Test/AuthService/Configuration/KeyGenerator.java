package Linktic.Test.AuthService.Configuration;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave HS256 segura
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Base64 Encoded Key: " + base64Key);
    }
}