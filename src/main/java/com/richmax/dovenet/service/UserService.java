package com.richmax.dovenet.service;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.data.UserDTO;

public interface UserService {

    /**
     * Registers a new user.
     *
     * @param username the username to register
     * @param email the email of the user
     * @param password the password (to be encoded if applicable)
     * @return the saved User entity
     */
    User registerUser(String username, String email, String password);
    User findByUsername(String username);
    User findById(Long id);
    UserDTO convertToDto(User user);
}
