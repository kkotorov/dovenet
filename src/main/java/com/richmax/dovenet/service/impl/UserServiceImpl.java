package com.richmax.dovenet.service.impl;

import com.richmax.dovenet.exception.UserAlreadyExistsException;
import com.richmax.dovenet.exception.UserNotFoundException;
import com.richmax.dovenet.mapper.UserMapper;
import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.repository.UserRepository;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
        user.setPassword(passwordEncoder.encode(password)); // ✅ encrypt password

        return userRepository.save(user);
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
}
