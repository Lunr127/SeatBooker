package com.example.seatbooker.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegisterDTO {
    
    @NotBlank(message = "学号不能为空")
    @Size(min = 5, max = 20, message = "学号长度必须在5-20之间")
    private String studentId;
    
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 30, message = "昵称长度必须在2-30之间")
    private String nickname;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
    
    @NotBlank(message = "院系ID不能为空")
    private Long departmentId;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;
} 