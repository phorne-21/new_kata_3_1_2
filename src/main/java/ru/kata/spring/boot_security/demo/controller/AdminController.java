package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        logger.info("hello from AdminController");
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("allRoles", roleService.getAllRoles());
        logger.info("getUsers method called in AdminController");
        return "admin/users";
    }

    @PostMapping
    public String addUser(@RequestParam String username,
                          @RequestParam String lastname,
                          @RequestParam String email,
                          @RequestParam int age,
                          @RequestParam String password,
                          @RequestParam List<String> roles) {

        User user = new User(username, lastname, age, email);
        user.setPassword(password);
        userService.saveUserWithRoles(user, roles);

        return "redirect:/admin";
    }


    @GetMapping("/update")
    public String showUserUpdatePage(@RequestParam("id") Long id,
                                     Model model) {
        User user = userService.findById(id);
        if (user == null) {
            logger.info("showUserUpdatePage method called in AdminController \n with id " + id + " not found");
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        logger.info("showUserUpdatePage method called in AdminController \n id " + id + " found, if's ok.");
        return "admin/update";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam(value = "username", required = false) String username,
                             @RequestParam(value = "lastname", required = false) String lastname,
                             @RequestParam(value = "email", required = false) String email,
                             @RequestParam(value = "age", required = false) Integer age,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "roles", required = false) List<String> roleNames) {

        User user = new User(username, lastname, age, email);
        user.setPassword(password);
        if (roleNames != null) {
            Set<Role> roles = roleNames.stream().map(roleService::findRoleByName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        userService.update(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        logger.info("deleteUser method called in AdminController \n user removed");
        return "redirect:/admin";
    }

    @GetMapping("/user/{id}")
    public String findUserById(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            logger.info("findUserById method called in AdminController \n with id " + id + " not found");
        }
        model.addAttribute("user", user);
        logger.info("getUser called in AdminController and it almost done");
        return "user/user";
    }
}