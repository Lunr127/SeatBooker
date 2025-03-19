package com.example.seatbooker.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("check_in")
public class CheckIn {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long reservationId;
    
    private Long userId;
    
    private LocalDateTime checkInTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
} 