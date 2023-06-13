package com.daelim.yourmind.user.service;



import com.daelim.yourmind.user.domain.Role;
import com.daelim.yourmind.user.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
