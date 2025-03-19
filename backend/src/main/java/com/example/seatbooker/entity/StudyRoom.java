package com.example.seatbooker.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("study_room")
public class StudyRoom {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String location;
    
    private Long departmentId;
    
    private Integer seatCount;
    
    private LocalTime openTime;
    
    private LocalTime closeTime;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
} 