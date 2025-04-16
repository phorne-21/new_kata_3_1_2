package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final UserService userService;

    public AdminController(UserService userService) {
        logger.info("hello from AdminController");
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("allRoles", userService.getAllRoles());
        logger.info("getUsers method called in AdminController");
        return "admin/users";
    }

    @PostMapping
    public String addUser(@RequestParam String username,
                          @RequestParam String lastname,
                          @RequestParam String email,
                          @RequestParam int age,
                          @RequestParam String password,
                          @RequestParam(required = false) List<String> roleNames) {
        userService.saveUserWithRoles(
                new User(username, lastname, age, email),
                password,
                roleNames);
        logger.info("addUser method  from AdminController");
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String showUserUpdatePage(@RequestParam("id") Long id,
                                     Model model) {
        model.addAllAttributes(userService.makeAllUserModelAttributes(id));
        logger.info("showUserUpdatePage method from AdminController");
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
        userService.update(
                id,
                new User(username, lastname, age, email),
                password,
                roleNames);
        logger.info("updateUser from AdminController");
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        logger.info("deleteUser method in AdminController");
        return "redirect:/admin";
    }

    @GetMapping("/user/{id}")
    public String findUserById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        logger.info("getUser called in AdminController and it almost done");
        return "user/user";
    }
}