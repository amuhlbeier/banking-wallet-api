package com.bankingapi.walletapi.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AdminDetailsService implements UserDetailsService {

    private final String hardcodedUsername = "admin";
    private final String encodedPassword;

    public AdminDetailsService(PasswordEncoder passwordEncoder) {
        this.encodedPassword = passwordEncoder.encode("adminpassword");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!username.equals(hardcodedUsername)) {
            throw new UsernameNotFoundException("Incorrect admin login");
        }
        return new User(hardcodedUsername, encodedPassword, Collections.emptyList());
    }
}
