package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Transactional
@Service
public class RoleServiceImpl implements RoleService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        logger.info("hello from RoleServiceImpl");
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
        logger.info("role saved in RoleServiceImpl");
    }

    @Override
    @Transactional(readOnly = true)
    public Role findRoleByName(String roleName) {
        logger.info("findRoleByName was called in RoleServiceImpl");
        return roleRepository.findByName(roleName);
    }

    public Set<Role> getDefaultRolesSet() {
        Set<Role> roles = new HashSet<>();
        roles.add(this.findRoleByName(Role.defaultRoleName));
        return roles;
    }

    @Override
    public Set<Role> getRolesSetByUserName(Set<Role> userRoles, List<String> roleNames) {
        Set<Role> roles;
        if (userRoles == null || userRoles.isEmpty()) {
            roles = getDefaultRolesSet();
        } else {
            roles = new HashSet<>();
            for (String roleName : roleNames) {
                Optional<Role> role = Optional.ofNullable(this.findRoleByName(roleName));
                if (role.isPresent()) {
                    roles.add(role.get());
                } else {
                    logger.warning("Role not found: " + roleName);
                }
            }
        }
        return roles;
    }
}
