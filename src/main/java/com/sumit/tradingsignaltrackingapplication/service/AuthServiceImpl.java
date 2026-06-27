package com.sumit.tradingsignaltrackingapplication.service;


import com.sumit.tradingsignaltrackingapplication.dto.RegisterRequest;
import com.sumit.tradingsignaltrackingapplication.entity.User;
import com.sumit.tradingsignaltrackingapplication.repository.UserRepository;
import com.sumit.tradingsignaltrackingapplication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }
}

