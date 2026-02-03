package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User not found");
        
        // Convert UserRole to GrantedAuthority
        // Spring Security expects "ROLE_" prefix for hasRole() checks
        String roleName = "ROLE_" + user.getRole().name();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
