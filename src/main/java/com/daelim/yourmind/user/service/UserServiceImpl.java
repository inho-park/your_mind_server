package com.daelim.yourmind.user.service;

import com.daelim.yourmind.user.domain.Role;
import com.daelim.yourmind.user.domain.RoleRepository;
import com.daelim.yourmind.user.domain.User;
import com.daelim.yourmind.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("username : {} ", username);
        }
        else {
            log.error("User not found in the DB");
            throw new UsernameNotFoundException("User not found in the DB");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        log.info("password : {}", user.getPassword());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public User saveUser(User user) {
        user = User.builder()
                .name(user.getName())
                .username(user.getUsername())
                .password(passwordEncoder.encode(user.getPassword()))
                .message(user.getMessage())
                .age(user.getAge())
                .isCounselor(user.isCounselor())
                .roles(user.getRoles())
                .build();
        return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        User user = userRepository.findByUsername(username).get();
        log.info("to user {}", username);
        Role role = roleRepository.findByName(roleName);
        log.info("Adding role {}", roleName);
        user.getRoles().add(role);
        log.info("success add Role");
    }

    @Override
    public User getUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UsernameNotFoundException("User not found in the DB");
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
