package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.UserAlreadyExistsException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.UserMapper;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.service.EmailService;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import com.richmax.dovenet.types.SubscriptionType;
import com.richmax.dovenet.types.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    @Override
    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        } else if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        user.setSubscription(SubscriptionType.FREE);
        user.setRole(UserRole.USER);
        user.setLanguage("en");

        user.setEmailVerified(false);

        this.generateAndSendVerificationEmail(user);

        return user;
    }

    @Override
    public void generateAndSendVerificationEmail(User user) {
        // Generate email verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        emailService.sendVerificationEmail(user);
    }

    @Override
    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserDTO convertToDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        return userMapper.toEntity(userDTO);
    }

    @Override
    public User changeEmail(String username, String newEmail, String currentPassword) {
        User user = findByUsername(username);

        // Verify password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Check if email is already used
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Update email
        user.setEmail(newEmail);

        // Reset verification state
        user.setEmailVerified(false);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        // Save updated user
        userRepository.save(user);

        // Send new verification email
        this.generateAndSendVerificationEmail(user);

        return user;
    }


    @Override
    public User changePassword(String username, String oldPassword, String newPassword) {
        User user = findByUsername(username);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User " + email + " not found"));

        // Generate a secure random token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // token valid for 1 hour
        userRepository.save(user);

        String resetLink = "https://www.dovenet.eu/reset-password?token=" + token;

        // Email contents
        String subject = "Password Reset Request";
        String body = "Hello " + user.getUsername() + ",\n\n"
                + "We received a request to reset your password. "
                + "Click the link below to set a new password:\n\n"
                + resetLink + "\n\n"
                + "This link will expire in 1 hour. If you didn’t request this, you can safely ignore it.\n\n"
                + "Best regards,\n"
                + "The Dovenet Team";

        // Send email
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false; // invalid or expired
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public UserDTO updateSubscription(String username, SubscriptionType type) {
        User user = findByUsername(username);
        user.setSubscription(type);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUserSettings(String username, UserDTO updates) {
        User user = findByUsername(username);

        // Update allowed fields only if present
        if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null) user.setLastName(updates.getLastName());
        if (updates.getPhoneNumber() != null) user.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getAddress() != null) user.setAddress(updates.getAddress());
        if (updates.getLanguage() != null) user.setLanguage(updates.getLanguage());

        // Save and return DTO
        return convertToDto(userRepository.save(user));
    }
}
