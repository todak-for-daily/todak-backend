package com.example.todak_server.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTtlMs;
    private final long refreshTtlMs;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKeyBase64,
            @Value("${jwt.access-ttl-ms}") long accessTtlMs,
            @Value("${jwt.refresh-ttl-ms}") long refreshTtlMs
    ) {
        byte[] decoded = Base64.getDecoder().decode(secretKeyBase64);
        if(decoded.length<32) throw new IllegalStateException("JWT key must be >= 32 bytes");
        this.key = Keys.hmacShaKeyFor(decoded);
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
    }

    // subject = {provider}:{providerId}
    public String createAccess(String subject, Long memberId, Collection<String> roles) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .claim("mid",memberId)
                .claim("roles",roles)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+accessTtlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefresh(String subject, Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .claim("mid", memberId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTtlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validate(String token) {
        try { parse(token); return true;}
        catch(JwtException | IllegalArgumentException e) {return false;}
    }

    public String getSubject(String token) { return parse(token).getBody().getSubject(); }
    public Long getMemberId(String token)  { return parse(token).getBody().get("mid", Long.class); }
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token){ return parse(token).getBody().get("roles", List.class); }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
