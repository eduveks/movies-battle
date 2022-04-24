package br.com.letscode.moviesbattle.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class JSONWebTokenService {
    public record AccessToken(String token) {}
    public record TokenInfo(String username, Collection<SimpleGrantedAuthority> authorities) {}

    @Value("${jwt.expirationInHours:24}")
    private Long expirationInHours;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public AccessToken buildAccessToken(Authentication authentication) {
        User principal = (User)authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        String accessToken = JWT.create()
                .withSubject(principal.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(
                        LocalDateTime.now().plusHours(expirationInHours)
                                .atZone(ZoneId.systemDefault()).toInstant()
                ))
                .withIssuer("movies-battle")
                .withClaim("roles", principal
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithm);
        return new AccessToken(accessToken);
    }

    public TokenInfo loadTokenInfo(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return new TokenInfo(username, authorities);
    }
}
