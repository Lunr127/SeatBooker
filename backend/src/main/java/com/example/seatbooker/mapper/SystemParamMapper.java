package com.example.seatbooker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seatbooker.entity.SystemParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemParamMapper extends BaseMapper<SystemParam> {
    
    @Select("SELECT param_value FROM system_param WHERE param_key = #{key} LIMIT 1")
    String getParamValue(@Param("key") String key);
} 