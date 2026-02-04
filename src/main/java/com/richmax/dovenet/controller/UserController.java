package com.richmax.dovenet.controller;

import com.richmax.dovenet.dto.*;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.security.JwtUtil;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        UserDTO dto = userService.convertToDto(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        Logger logger =  Logger.getLogger(this.getClass().getName());
        logger.log(Level.INFO, "Verifying email verification token: " + token);
        if (verified) {
            logger.info("Email verified");
            return ResponseEntity.ok("Email successfully verified. You can now log in.");
        } else {
            logger.info("Email not verified");
            return ResponseEntity.badRequest().body("Invalid or expired verification token.");
        }
    }

    @GetMapping("/trigger-verify")
    public ResponseEntity<String> triggerEmailVerification(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email is already verified");
        }

        // Generate a new verification token and send email
        userService.generateAndSendVerificationEmail(user);

        return ResponseEntity.ok("Verification email sent successfully");
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return userService.convertToDto(user);
    }

    @PatchMapping("/me/change-email")
    public ResponseEntity<UserDTO> changeEmail(Authentication authentication,
                                               @RequestBody ChangeEmailRequest request) {
        User updatedUser = userService.changeEmail(authentication.getName(), request.getNewEmail(), request.getPassword());
        return ResponseEntity.ok(userService.convertToDto(updatedUser));
    }

    @PatchMapping("/me/change-password")
    public ResponseEntity<UserDTO> changePassword(Authentication authentication,
                                                  @RequestBody ChangePasswordRequest request) {
        User updatedUser = userService.changePassword(authentication.getName(), request.getOldPassword(), request.getNewPassword());
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

    @PatchMapping("/me/subscription")
    public UserDTO updateSubscription(
            @RequestParam SubscriptionType type,
            Authentication authentication
    ) {
        return userService.updateSubscription(authentication.getName(), type);
    }


    @PatchMapping("/me/update-settings")
    public UserDTO updateUserSettings(
            @RequestBody UserDTO updates,
            Authentication authentication
    ) {
        return userService.updateUserSettings(authentication.getName(), updates);
    }

    @GetMapping("/me/subscription-status")
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        boolean isActive = userService.hasActiveSubscription(user);
        
        return ResponseEntity.ok(Map.of(
            "active", isActive,
            "type", user.getSubscription(),
            "validUntil", user.getSubscriptionValidUntil() != null ? user.getSubscriptionValidUntil() : "null",
            "autoRenew", user.isAutoRenew()
        ));
    }
}
