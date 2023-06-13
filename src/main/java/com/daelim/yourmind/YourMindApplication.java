package com.daelim.yourmind;

import com.daelim.yourmind.user.domain.Role;
import com.daelim.yourmind.user.domain.User;
import com.daelim.yourmind.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@EnableJpaAuditing
@SpringBootApplication
public class YourMindApplication {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(YourMindApplication.class, args);
    }

//    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_COUNSELOR"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));

            userService.saveUser(new User(null, "nameA", "usernameA", "passwordA", "messageA", 1, true, new ArrayList<>()));
            userService.saveUser(new User(null, "nameB", "usernameB", "passwordB", "messageB", 2, true, new ArrayList<>()));
            userService.saveUser(new User(null, "nameC", "usernameC", "passwordC", "messageC", 3, false, new ArrayList<>()));
            userService.saveUser(new User(null, "nameD", "usernameD", "passwordD", "messageD", 4, false, new ArrayList<>()));

            userService.addRoleToUser("usernameA", "ROLE_USER");

            userService.addRoleToUser("usernameB", "ROLE_USER");
            userService.addRoleToUser("usernameB", "ROLE_MANAGER");

            userService.addRoleToUser("usernameC", "ROLE_MANAGER");
            userService.addRoleToUser("usernameC", "ROLE_ADMIN");

            userService.addRoleToUser("usernameD", "ROLE_USER");
            userService.addRoleToUser("usernameD", "ROLE_MANAGER");
            userService.addRoleToUser("usernameD", "ROLE_ADMIN");
            userService.addRoleToUser("usernameD", "ROLE_COUNSELOR");
        };
    }
}
