package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAll();

    void save(User user);

    void update(Long id, User user, String password, List<String> roleNames);

    void delete(Long id);

    User findById(Long id);

    void saveUserWithRoles(User user, String password, List<String> roleNames);

    List<Role> getAllRoles();

    Map<String, Object> makeAllUserModelAttributes(Long userId);
}
