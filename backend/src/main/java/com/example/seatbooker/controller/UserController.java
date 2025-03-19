package com.example.seatbooker.controller;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.UserLoginDTO;
import com.example.seatbooker.dto.UserRegisterDTO;
import com.example.seatbooker.entity.User;
import com.example.seatbooker.service.UserService;
import com.example.seatbooker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody @Validated UserRegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
    
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated UserLoginDTO loginDTO) {
        return userService.login(loginDTO);
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        // 从SecurityContext中获取用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 从JWT中获取用户ID
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        return userService.getUserInfo(userId);
    }
    
    @PutMapping("/info")
    public Result<Boolean> updateUserInfo(@RequestBody User user) {
        // 从SecurityContext中获取用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 从JWT中获取用户ID
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        // 设置用户ID，防止修改他人信息
        user.setId(userId);
        
        return userService.updateUserInfo(user);
    }
} 