package com.example.seatbooker.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("system_param")
public class SystemParam {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String paramKey;
    
    private String paramValue;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
} 