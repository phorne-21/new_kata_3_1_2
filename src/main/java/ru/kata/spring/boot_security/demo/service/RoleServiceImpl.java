package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.List;
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
}
