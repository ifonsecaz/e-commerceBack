package com.ecommerce.userservice.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.Collections;

import com.ecommerce.userservice.repository.*;
import com.ecommerce.userservice.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository userRepository;

 public CustomUserDetailsService(UserRepository userRepository) {
 this.userRepository = userRepository;
 }

 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
        throw new UsernameNotFoundException("User not found");
    }
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole()))
    );
 }
}


