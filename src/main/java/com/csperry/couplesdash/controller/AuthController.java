package com.csperry.couplesdash.controller;

import com.csperry.couplesdash.DTO.UserDto;
import com.csperry.couplesdash.model.AuthRequest;
import com.csperry.couplesdash.model.AuthResponse;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.repository.UserRepository;
import com.csperry.couplesdash.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Duration;

import static com.csperry.couplesdash.constants.APIConstants.AUTH_URL;
import static com.csperry.couplesdash.constants.APIConstants.LOGIN_ENDPOINT;
import static com.csperry.couplesdash.constants.APIConstants.LOGOUT_ENDPOINT;
import static com.csperry.couplesdash.constants.APIConstants.REGISTER_ENDPOINT;

@RestController
@CrossOrigin
@RequestMapping(AUTH_URL)
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(REGISTER_ENDPOINT)
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        logger.info("/register endpoint hit");
        User created = authService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(LOGIN_ENDPOINT)
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        logger.info("/login endpoint hit");

        AuthResponse authResponse = authService.login(request);
        String jwtToken = authResponse.getToken();

        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(10))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        logger.info("/me endpoint hit");

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        return ResponseEntity.ok(new UserDto(user));
    }


    @PostMapping(LOGOUT_ENDPOINT)
    public ResponseEntity<?> logout(HttpServletResponse response) {
        logger.info("/logout endpoint hit");
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out");
    }
}
