package br.com.letscode.moviesbattle.controller.util;

import br.com.letscode.moviesbattle.data.model.User;
import br.com.letscode.moviesbattle.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticatedController {
    @Autowired
    protected UserService userService;

    protected User getUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return userService.getUser(userName);
    }

    public record ErrorResult(boolean error, String code) { }
}
