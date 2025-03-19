package com.example.seatbooker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seatbooker.entity.Department;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {
} 