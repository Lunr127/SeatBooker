package com.example.seatbooker.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CheckInDTO {
    
    @NotNull(message = "自习室ID不能为空")
    private Long roomId;
    
    @NotBlank(message = "验证码不能为空")
    private String code;
    
    private Long userId;
    
    private Long seatId;
} 