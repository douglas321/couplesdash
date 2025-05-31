package com.csperry.couplesdash.controller;

import com.csperry.couplesdash.model.AuthRequest;
import com.csperry.couplesdash.model.AuthResponse;
import com.csperry.couplesdash.model.User;
import com.csperry.couplesdash.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.csperry.couplesdash.constants.APIConstants.AUTH_URL;
import static com.csperry.couplesdash.constants.APIConstants.LOGIN_ENDPOINT;
import static com.csperry.couplesdash.constants.APIConstants.REGISTER_ENDPOINT;

@RestController
@CrossOrigin
@RequestMapping(AUTH_URL)
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        logger.info("/login endpoint hit");
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


}
