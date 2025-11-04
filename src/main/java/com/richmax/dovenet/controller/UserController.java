package com.richmax.dovenet.controller;

import com.richmax.dovenet.dto.*;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.security.JwtUtil;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        UserDTO dto = userService.convertToDto(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);
        User user = userService.findByUsername(username);
        return userService.convertToDto(user);
    }

    @PatchMapping("/me/change-email")
    public ResponseEntity<UserDTO> changeEmail(@RequestHeader("Authorization") String authHeader,
                                               @RequestBody ChangeEmailRequest request) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User updatedUser = userService.changeEmail(username, request.getNewEmail(), request.getPassword());
        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<UserDTO> changePassword(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody ChangePasswordRequest request) {
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));
        User updatedUser = userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset email sent if the email exists");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = userService.resetPassword(request.getToken(), request.getNewPassword());
        if (!success) {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
        return ResponseEntity.ok("Password successfully reset");
    }
}
