package com.example.tp3logging.controllers;
import com.example.tp3logging.dto.LoginCredentials;
import com.example.tp3logging.models.User;
import com.example.tp3logging.repositories.UserRepository;
import com.example.tp3logging.services.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpSession;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JWTService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JWTService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid
    @RequestBody
    User user) {
        logger.info("{  \"operation\": \"AUTHENTICATE\"}");
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException(("User with email " + user.getEmail()) + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody
    LoginCredentials loginCredentials, HttpServletRequest request) {
        logger.info("{  \"operation\": \"AUTHENTICATE\"}");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginCredentials.getEmail(), loginCredentials.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        request.getSession().setAttribute("user", authentication.getPrincipal());// Add user to session

        User user = userRepository.findByEmail(loginCredentials.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
        return ResponseEntity.ok(jwtService.generateToken(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        logger.info("{  \"operation\": \"AUTHENTICATE\"}");
        session.invalidate();
        return ResponseEntity.ok("User logged out successfully");
    }

    private Logger logger = org.apache.logging.log4j.LogManager.getLogger(AuthController.class);
}