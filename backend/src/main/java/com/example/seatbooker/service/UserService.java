package com.example.seatbooker.service;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.UserLoginDTO;
import com.example.seatbooker.dto.UserRegisterDTO;
import com.example.seatbooker.entity.User;

public interface UserService {
    
    Result<String> register(UserRegisterDTO registerDTO);
    
    Result<String> login(UserLoginDTO loginDTO);
    
    Result<User> getUserInfo(Long userId);
    
    Result<Boolean> updateUserInfo(User user);
} 