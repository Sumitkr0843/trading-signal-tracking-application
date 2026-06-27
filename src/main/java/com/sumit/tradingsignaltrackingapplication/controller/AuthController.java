package com.sumit.tradingsignaltrackingapplication.controller;

import com.sumit.tradingsignaltrackingapplication.dto.AuthRequest;
import com.sumit.tradingsignaltrackingapplication.dto.AuthResponse;
import com.sumit.tradingsignaltrackingapplication.dto.RegisterRequest;
import com.sumit.tradingsignaltrackingapplication.service.AppUserDetailsService;
import com.sumit.tradingsignaltrackingapplication.service.AuthService;
import com.sumit.tradingsignaltrackingapplication.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(request.getEmail());

            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(
                    new AuthResponse(
                            request.getEmail(),
                            token
                    )
            );

        } catch (BadCredentialsException ex) {

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid email or password.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        } catch (DisabledException ex) {

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "User account is disabled.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error);

        } catch (Exception ex) {

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}