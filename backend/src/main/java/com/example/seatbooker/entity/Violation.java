package com.example.seatbooker.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("violation")
public class Violation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long reservationId;
    
    private LocalDateTime violationTime;
    
    private String reason;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
} 