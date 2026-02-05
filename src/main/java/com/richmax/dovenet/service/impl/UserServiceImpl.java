package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.UserAlreadyExistsException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.UserMapper;
import com.richmax.dovenet.repository.*;
import com.richmax.dovenet.repository.data.EmailChangeRequest;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.EmailService;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import com.richmax.dovenet.types.SubscriptionType;
import com.richmax.dovenet.types.UserRole;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
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
    private final CompetitionRepository competitionRepository;
    private final LoftRepository loftRepository;
    private final BreedingSeasonRepository breedingSeasonRepository;
    private final EmailChangeRequestRepository emailChangeRequestRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, EmailService emailService,
                           CompetitionRepository competitionRepository, LoftRepository loftRepository, BreedingSeasonRepository breedingSeasonRepository,
                           EmailChangeRequestRepository emailChangeRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.competitionRepository = competitionRepository;
        this.loftRepository = loftRepository;
        this.breedingSeasonRepository = breedingSeasonRepository;
        this.emailChangeRequestRepository = emailChangeRequestRepository;
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
        // Set 10-day free trial
        user.setSubscriptionValidUntil(LocalDateTime.now().plusDays(10));
        
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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    public UserDTO convertToDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public void initiateEmailChange(String currentEmail, String newEmail, String password) {
        User user = findByEmail(currentEmail);

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Check if new email is already used
        if (userRepository.findByEmail(newEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create change request
        EmailChangeRequest request = new EmailChangeRequest();
        request.setUser(user);
        request.setNewEmail(newEmail);
        request.setToken(UUID.randomUUID().toString());
        request.setExpiryDate(LocalDateTime.now().plusHours(1));

        emailChangeRequestRepository.save(request);

        // Send confirmation to NEW email
        emailService.sendEmailChangeConfirmation(newEmail, request.getToken());
    }

    @Override
    @Transactional
    public boolean finalizeEmailChange(String token) {
        EmailChangeRequest request = emailChangeRequestRepository.findByToken(token);
        
        if (request == null || request.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = request.getUser();
        String oldEmail = user.getEmail();
        
        // Update email
        user.setEmail(request.getNewEmail());
        user.setEmailVerified(true); // New email is verified by clicking the link
        userRepository.save(user);

        // Notify OLD email
        emailService.sendEmailChangeNotification(oldEmail);

        // Clean up request
        emailChangeRequestRepository.delete(request);
        
        return true;
    }

    @Override
    public User changePassword(String email, String oldPassword, String newPassword) {
        User user = findByEmail(email);

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);
        
        // Notify user
        emailService.sendPasswordChangedNotification(email);
        
        return savedUser;
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
        
        // Notify user
        emailService.sendPasswordChangedNotification(user.getEmail());

        return true;
    }

    @Transactional
    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Manually delete related entities to avoid foreign key constraints
        // 1. Competitions
        competitionRepository.deleteByOwner(user);
        
        // 2. Lofts
        loftRepository.deleteByOwner(user);
        
        // 3. Breeding Seasons (and pairs/offspring via cascade if configured in DB, otherwise manually)
        breedingSeasonRepository.deleteByOwner(user);

        // 4. Pigeons (handled by cascade in User entity or manual if not)
        if (user.getPigeons() != null) {
            user.getPigeons().removeIf(p -> p.getId() == null);
        }

        // Now delete the user
        userRepository.delete(user);
    }

    @Override
    public UserDTO updateSubscription(String email, SubscriptionType type) {
        User user = findByEmail(email);
        user.setSubscription(type);
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUserSettings(String email, UserDTO updates) {
        User user = findByEmail(email);

        // Update allowed fields only if present
        if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null) user.setLastName(updates.getLastName());
        if (updates.getPhoneNumber() != null) user.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getAddress() != null) user.setAddress(updates.getAddress());
        if (updates.getLanguage() != null) user.setLanguage(updates.getLanguage());

        // Save and return DTO
        return convertToDto(userRepository.save(user));
    }

    @Override
    public String getOrCreateStripeCustomer(User user) {
        if (user.getStripeCustomerId() != null) {
            return user.getStripeCustomerId();
        }
        try {
            Customer customer = Customer.create(CustomerCreateParams.builder().setEmail(user.getEmail()).setName(user.getUsername()).build());
            user.setStripeCustomerId(customer.getId());
            userRepository.save(user);
            return customer.getId();
        } catch (Exception e){
            // Include the original exception cause
            throw new RuntimeException("Stripe customer creation failed: " + e.getMessage(), e);
        }
    }

    // TODO: Enforce this check in API endpoints (Backend Protection).
    // Currently, restrictions are only applied on the frontend.
    public boolean hasActiveSubscription(User user) {
        // Check if subscription is valid (works for FREE trial and PAID subscriptions)
        return user.getSubscriptionValidUntil() != null
                && user.getSubscriptionValidUntil().isAfter(LocalDateTime.now());
    }

    @Override
    public void expireSubscriptionNow(String email) {
        User user = findByEmail(email);
        // Set to yesterday
        user.setSubscriptionValidUntil(LocalDateTime.now().minusDays(1));
        userRepository.save(user);
    }

}
