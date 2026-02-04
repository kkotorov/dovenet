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
    
    User findByEmail(String email);

    UserDTO convertToDto(User user);

    User changeEmail(String email, String newEmail, String currentPassword);

    User changePassword(String email, String oldPassword, String newPassword);

    void initiatePasswordReset(String email);

    boolean resetPassword(String token, String newPassword);

    void deleteUserByEmail(String email);

    UserDTO updateSubscription(String email, SubscriptionType type);

    @Transactional
    UserDTO updateUserSettings(String email, UserDTO updates);

    String getOrCreateStripeCustomer(User user);

    boolean hasActiveSubscription(User user);

    void expireSubscriptionNow(String email);
}
