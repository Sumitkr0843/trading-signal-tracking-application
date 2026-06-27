package com.sumit.tradingsignaltrackingapplication.service;

import com.sumit.tradingsignaltrackingapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.sumit.tradingsignaltrackingapplication.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User existingUser = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(existingUser.getEmail())
                .password(existingUser.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }
}
