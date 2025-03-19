package com.example.seatbooker.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {
    
    @NotBlank(message = "学号不能为空")
    private String studentId;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}