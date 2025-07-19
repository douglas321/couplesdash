package com.csperry.couplesdash.service;

import com.csperry.couplesdash.model.AuthRequest;
import com.csperry.couplesdash.model.AuthResponse;
import com.csperry.couplesdash.model.Couple;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.repository.CoupleRepository;
import com.csperry.couplesdash.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       CoupleRepository coupleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.coupleRepository = coupleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.info("Email already in use: {}", user.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        logger.info("Creating new user with email: {}", user.getEmail());

        // Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Create and assign a new couple
        Couple couple = new Couple();
        coupleRepository.save(couple);
        user.setCouple(couple);

        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        logger.info("Authenticating login attempt for email: {}", request.getEmail());
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for {}: {}", request.getEmail(), e.getClass().getSimpleName());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        logger.info("Authentication successful");
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
