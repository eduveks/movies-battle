package br.com.letscode.moviesbattle.filter;

import br.com.letscode.moviesbattle.service.JSONWebTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static br.com.letscode.moviesbattle.config.JSONWebTokenConstants.INPUT_PASSWORD_KEY;
import static br.com.letscode.moviesbattle.config.JSONWebTokenConstants.INPUT_USERNAME_KEY;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JSONWebTokenService jsonWebTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> login;
        try {
            login = mapper.readValue(request.getInputStream(), Map.class);
        } catch (IOException e) {
            log.error("Failed to parse the login data.", e);
            return null;
        }
        final var username = login.get(INPUT_USERNAME_KEY);
        final var password = login.get(INPUT_PASSWORD_KEY);
        log.info(
                "New login attempt: {{}: \"{}\", {}: \"{}\"",
                INPUT_USERNAME_KEY, username,
                INPUT_PASSWORD_KEY, password
        );
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        final var accessToken = jsonWebTokenService.buildAccessToken(authentication);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), accessToken);
    }
}
