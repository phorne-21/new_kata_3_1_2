package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // возможны ошибки, если name не уникально, поэтому ниже ещё метод
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showUserInfo(Model model,
                               Authentication authentication) {
        model.addAttribute("user",
                userService.findByUsername(authentication.getName()));
        return "user/user";
    }
}
