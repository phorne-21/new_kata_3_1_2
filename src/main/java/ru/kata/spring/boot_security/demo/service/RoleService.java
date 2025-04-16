package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> getAllRoles();

    void saveRole(Role role);

    Role findRoleByName(String roleName);

    Set<Role> getRolesSetByUserName(Set<Role> userRoles, List<String> roleNames);
}
