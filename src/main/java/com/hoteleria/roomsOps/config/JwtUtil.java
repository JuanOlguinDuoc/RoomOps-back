package com.hoteleria.roomsOps.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

// Componente Spring disponible para inyección en cualquier clase de la aplicación
@Component
public class JwtUtil {

    // Clave secreta leída desde application.properties (jwt.secret).
    // Debe tener mínimo 32 caracteres para HS256.
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de vida del token en milisegundos. Por defecto 1 hora (3 600 000 ms).
    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    // Convierte el secret en un objeto SecretKey compatible con HMAC-SHA256.
    // Se usa StandardCharsets.UTF_8 para evitar diferencias entre entornos.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Genera un token firmado con el email del usuario como subject.
    // Incluye fecha de emisión y fecha de expiración calculada desde ahora.
    public String generadorToken(String email) {
        return Jwts.builder()
                .subject(email)                                                        // identifica al usuario
                .issuedAt(new Date())                                                  // momento de creación
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))      // momento de vencimiento
                .signWith(getSigningKey(), Jwts.SIG.HS256)                             // firma con HMAC-SHA256
                .compact();                                                             // serializa a String
    }

    // Extrae el email (subject) del payload del token.
    // Si el token está corrupto o expirado, JJWT lanza una excepción antes de llegar aquí.
    public String obtenerCorreo(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())     // define la clave para verificar la firma
                .build()
                .parseSignedClaims(token)        // verifica firma y parsea el token
                .getPayload()                    // accede al body (claims)
                .getSubject();                   // devuelve el campo "sub"
    }

    // Valida que el token tenga firma correcta y no haya expirado.
    // Devuelve false ante cualquier error: firma inválida, expiración, formato incorrecto, etc.
    public boolean validacionToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException cubre: firma inválida, token expirado, malformado, etc.
            return false;
        }
    }

    // Extrae la fecha de expiración del token.
    // Útil para mostrar al cliente cuándo vence su sesión.
    public Date extraerExpiracion(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();   // campo "exp" del JWT
    }
}