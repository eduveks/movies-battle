package br.com.letscode.moviesbattle.filter;

import br.com.letscode.moviesbattle.service.JSONWebTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.com.letscode.moviesbattle.config.JSONWebTokenConstants.*;

@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JSONWebTokenService jsonWebTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
        } else {
            final var authorizationHeader = request.getHeader(HEADER_KEY);
            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
                try {
                    final var token = authorizationHeader.substring(TOKEN_PREFIX.length());
                    final var tokenInfo = jsonWebTokenService.loadTokenInfo(token);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(tokenInfo.username(), null, tokenInfo.authorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                } catch (Exception ex) {
                    log.error("Login error in: {}", ex.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    final var errorMessage = ex.getMessage();
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), errorMessage);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
