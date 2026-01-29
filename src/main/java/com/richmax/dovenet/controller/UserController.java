package com.richmax.dovenet.controller;

import com.richmax.dovenet.dto.*;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.security.JwtUtil;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
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
    public ResponseEntity<String> triggerEmailVerification(@RequestHeader("Authorization") String authHeader) {
        // Extract username from JWT
        String username = jwtUtil.extractUsername(authHeader.replace("Bearer ", ""));

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        User user = optionalUser.get();

        if (user.isEmailVerified()) {
            return ResponseEntity.badRequest().body("Email is already verified");
        }

        // Generate a new verification token and send email
        userService.generateAndSendVerificationEmail(user);

        return ResponseEntity.ok("Verification email sent successfully");
    }


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    @PatchMapping("/me/subscription")
    public UserDTO updateSubscription(
            @RequestParam SubscriptionType type,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return userService.updateSubscription(username, type);
    }


    @PatchMapping("/me/update-settings")
    public UserDTO updateUserSettings(
            @RequestBody UserDTO updates,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return userService.updateUserSettings(username, updates);
    }

    @GetMapping("/me/subscription-status")
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        boolean isActive = userService.hasActiveSubscription(user);
        
        return ResponseEntity.ok(Map.of(
            "active", isActive,
            "type", user.getSubscription(),
            "validUntil", user.getSubscriptionValidUntil() != null ? user.getSubscriptionValidUntil() : "null",
            "autoRenew", user.isAutoRenew()
        ));
    }

    // FOR TESTING ONLY
    @PostMapping("/me/expire-subscription")
    public ResponseEntity<String> expireSubscription(Authentication authentication) {
        userService.expireSubscriptionNow(authentication.getName());
        return ResponseEntity.ok("Subscription expired for testing.");
    }
}
