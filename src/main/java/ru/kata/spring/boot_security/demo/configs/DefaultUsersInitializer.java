package ru.kata.spring.boot_security.demo.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultUsersInitializer {
    private final UserService userService;
    private final RoleService roleService;

    public DefaultUsersInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        try {
            if (userService.findAll().isEmpty()) {
                Role roleAdmin = new Role("ROLE_ADMIN");
                Role roleUser = new Role("ROLE_USER");
                
                roleService.saveRole(roleAdmin);
                roleService.saveRole(roleUser);

                User admin = new User();
                admin.setUsername("admin");
                admin.setLastname("admin");
                admin.setEmail("admin@admin.com");
                admin.setAge(30);
                // todo encode -?
                admin.setPassword("admin");
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleAdmin);
                admin.setRoles(adminRoles);

                User user = new User();
                user.setUsername("user");
                user.setLastname("user");
                user.setEmail("user@user.com");
                user.setAge(25);
                // todo encode -?
                user.setPassword("user");
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleUser);
                user.setRoles(userRoles);

                userService.save(admin);
                userService.save(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}