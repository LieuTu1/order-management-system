package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.entity.User;
import com.example.ordermanagementsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Logger dùng để ghi log
    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Tìm user theo username để Spring Security xác thực
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found, username={}", username);
                    return new UsernameNotFoundException("User not found");
                });

        log.info("User loaded successfully, username={}", username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getRole())
                .build();
    }
}