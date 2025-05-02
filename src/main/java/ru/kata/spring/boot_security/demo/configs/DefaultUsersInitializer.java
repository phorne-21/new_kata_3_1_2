package ru.kata.spring.boot_security.demo.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.DTO.UserCreateDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultUsersInitializer {
    private final UserDTOService userService;
    private final RoleService roleService;

    public DefaultUsersInitializer(UserDTOService userService, RoleService roleService) {
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

                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleAdmin);

                UserCreateDTO admin = new UserCreateDTO("admin",
                        "admin",
                        20,
                        "admin@admin.com",
                        "admin",
                        adminRoles);

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleUser);

                UserCreateDTO user = new UserCreateDTO(
                        "test_user",
                        "test_user",
                        30,
                        "test@test.com",
                        "user",
                        userRoles);

                userService.create(admin);
                userService.create(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}