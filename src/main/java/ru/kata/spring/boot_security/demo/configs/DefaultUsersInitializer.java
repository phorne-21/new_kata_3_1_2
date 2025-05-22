package ru.kata.spring.boot_security.demo.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.DTO.UserCreateDTO;
import ru.kata.spring.boot_security.demo.model.Role;
//import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.DTO.UserDTOService;
import ru.kata.spring.boot_security.demo.service.RoleService;
//import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultUsersInitializer {
        private final UserDTOService userService;
//    private final UserService userService;
    private final RoleService roleService;

//    public DefaultUsersInitializer(UserDTOService userService, RoleService roleService) {
//        this.userService = userService;
//        this.roleService = roleService;
//    }
//
//    @PostConstruct
//    public void init() {
//        try {
//            if (userService.findAll().isEmpty()) {
//                Role roleAdmin = new Role("ROLE_ADMIN");
//                Role roleUser = new Role("ROLE_USER");
//
//                roleService.saveRole(roleAdmin);
//                roleService.saveRole(roleUser);
//
//                Set<Role> adminRoles = new HashSet<>();
//                adminRoles.add(roleAdmin);
//
//                User admin = new User();
//                admin.setFirstName("admin");
//                admin.setLastName("admin");
//                admin.setAge(20);
//                admin.setEmail("admin@admin.com");
//                admin.setPassword("admin");
//                admin.setRoles(adminRoles);
//
//                Set<Role> userRoles = new HashSet<>();
//                userRoles.add(roleUser);
//
//                User user = new User();
//                user.setFirstName("user");
//                user.setLastName("user");
//                user.setAge(30);
//                user.setEmail("user@user.com");
//                user.setPassword("user");
//                user.setRoles(userRoles);
//
//                userService.create(admin);
//                userService.create(user);
////                userService.save(admin);
////                userService.save(user);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
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
                        "user",
                        "user",
                        30,
                        "user@user.com",
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