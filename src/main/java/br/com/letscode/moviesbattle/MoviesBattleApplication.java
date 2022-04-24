package br.com.letscode.moviesbattle;

import br.com.letscode.moviesbattle.data.model.Role;
import br.com.letscode.moviesbattle.data.model.User;
import br.com.letscode.moviesbattle.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class MoviesBattleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviesBattleApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new User(null, "user1", "user1", "123", 0, 0, 0, new ArrayList<>(), new ArrayList<>()));
			userService.saveUser(new User(null, "user2", "user2", "123", 0, 0, 0, new ArrayList<>(), new ArrayList<>()));
			userService.saveUser(new User(null, "user3", "user3", "123", 0, 0, 0, new ArrayList<>(), new ArrayList<>()));
			userService.saveUser(new User(null, "user4", "user4", "123", 0, 0, 0, new ArrayList<>(), new ArrayList<>()));

			userService.addRoleToUser("user1", "ROLE_USER");
			userService.addRoleToUser("user2", "ROLE_USER");
			userService.addRoleToUser("user3", "ROLE_USER");
			userService.addRoleToUser("user4", "ROLE_USER");
		};
	}
}
