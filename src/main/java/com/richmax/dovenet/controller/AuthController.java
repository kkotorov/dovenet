package com.richmax.dovenet.controller;

import com.richmax.dovenet.dto.LoginRequest;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.security.JwtUtil;
import com.richmax.dovenet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid username or password");
        }

        // Track login
        user.setLastLoginDate(LocalDateTime.now());
        user.setLastLoginIp(httpRequest.getRemoteAddr());
        user.setLastLoginUserAgent(httpRequest.getHeader("User-Agent"));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());
        return Map.of("token", token);
    }
}
