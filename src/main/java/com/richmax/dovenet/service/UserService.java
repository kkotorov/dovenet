package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.data.UserDTO;
import com.richmax.dovenet.types.SubscriptionType;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    /**
     * Registers a new user.
     *
     * @param username the username to register
     * @param email    the email of the user
     * @param password the password (to be encoded if applicable)
     * @return the saved User entity
     */
    User registerUser(String username, String email, String password);

    void generateAndSendVerificationEmail(User user);

    boolean verifyEmail(String token);

    User findByUsername(String username);

    User findById(Long id);

    UserDTO convertToDto(User user);

    User convertToEntity(UserDTO userDTO);

    User changeEmail(String username, String newEmail, String currentPassword);

    User changePassword(String username, String oldPassword, String newPassword);

    void initiatePasswordReset(String email);

    boolean resetPassword(String token, String newPassword);

    void deleteUser(String username);

    UserDTO updateSubscription(String username, SubscriptionType type);

    @Transactional
    UserDTO updateUserSettings(String username, UserDTO updates);

    String getOrCreateStripeCustomer(User user);

    boolean hasActiveSubscription(User user);

    // FOR TESTING ONLY
    void expireSubscriptionNow(String username);
}
