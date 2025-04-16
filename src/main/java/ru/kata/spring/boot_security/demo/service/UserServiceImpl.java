package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        logger.info("hello from UserServiceImpl");
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.info("findAll was called in UserServiceImpl");
        return userRepository.findAll();
    }

    @Override
    public void saveUserWithRoles(User user, String password, List<String> roleNames) {
        logger.info("saveUserWithRoles was called in UserServiceImpl");
        logger.info("Roles: " + (roleNames != null ? roleNames : "null"));
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(getRolesSetByUserName(roleNames));
        userRepository.save(user);
        logger.info("User saved in UserServiceImpl");
        logger.info(user.toString() + " " + user.getRoles().toString());
    }

    public void save(User user) {
        logger.info("save was called in UserServiceImpl");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleService.findRoleByName(Role.defaultRoleName));
            user.setRoles(userRoles);
        }
        userRepository.save(user);
        logger.info("User saved in UserServiceImpl");
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        logger.info("findById was called in UserServiceImpl");
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found, id = " + id));
    }

    @Override
    public void update(Long id, User user, String password, List<String> roleNames) {
        logger.info("update was called in UserServiceImpl");
        User u = userRepository.findById(id).orElseThrow();
        if (user.getUsername() != null) {
            u.setUsername(user.getUsername());
        }
        if (user.getLastname() != null) {
            u.setLastname(user.getLastname());
        }
        if (user.getAge() != null) {
            u.setAge(user.getAge());
        }
        if (user.getEmail() != null) {
            u.setEmail(user.getEmail());
        }
        if (password != null) {
            u.setPassword(passwordEncoder.encode(password));
        }
        if (roleNames != null) {
            u.setRoles(getRolesSetByUserName(roleNames));
        }
        userRepository.save(u);
        logger.info("user updated in UserServiceImpl");
    }

    @Override
    public void delete(Long id) {
        logger.info("delete was called in UserServiceImpl");
        userRepository.deleteById(id);
        logger.info("user deleted in UserServiceImpl");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername was called in UserServiceImpl");
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        logger.info("findByUsername was called in UserServiceImpl");
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> makeAllUserModelAttributes(Long userId) {
        logger.info("makeAllUserModelAttributes was called in UserServiceImpl");
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found, id = " + userId));
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("allRoles", roleService.getAllRoles());
        return model;
    }

    private Set<Role> getRolesSetByUserName(List<String> roleNames) {
        logger.info("getRolesSetByUserName was called in UserServiceImpl");
        Set<Role> userRoles = new HashSet<>();
        if (roleNames != null && !roleNames.isEmpty()) {
            userRoles = roleNames.stream().map(roleService::findRoleByName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } else {
            userRoles.add(roleService.findRoleByName(Role.defaultRoleName));
        }
        return userRoles;
    }
}
