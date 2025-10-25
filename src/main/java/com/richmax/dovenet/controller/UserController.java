package com.richmax.dovenet.controller;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.UserService;
import com.richmax.dovenet.service.data.RegisterRequest;
import com.richmax.dovenet.service.data.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        UserDTO dto = userService.convertToDto(user);
        return ResponseEntity.ok(dto);
    }


    // Get user by username
    @GetMapping("/{username}")
    public UserDTO getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Map User -> UserDto
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
