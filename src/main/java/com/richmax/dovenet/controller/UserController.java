package com.richmax.dovenet.controller;

import com.richmax.dovenet.repository.data.User;
import com.richmax.dovenet.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> request) {
        String username =  request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        return userService.registerUser(username, email, password);
    }
}
