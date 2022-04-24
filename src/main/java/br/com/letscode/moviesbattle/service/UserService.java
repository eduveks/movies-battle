package br.com.letscode.moviesbattle.service;

import br.com.letscode.moviesbattle.data.model.Role;
import br.com.letscode.moviesbattle.data.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
