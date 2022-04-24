package br.com.letscode.moviesbattle.controller;

import br.com.letscode.moviesbattle.service.JSONWebTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JSONWebTokenService jsonWebTokenService;

    @Operation(summary = "User sign in with JWT (JSON Web Token).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The token to be used in further requests.")
    })
    @PostMapping({"/login"})
    public JSONWebTokenService.AccessToken login(@RequestBody LoginForm loginForm) throws UnsupportedOperationException {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginForm.username, loginForm.password);
        var authentication = authenticationManager.authenticate(authToken);
        return jsonWebTokenService.buildAccessToken(authentication);
    }

    private record LoginForm(String username, String password) {}
}
