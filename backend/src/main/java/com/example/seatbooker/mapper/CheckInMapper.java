package com.example.seatbooker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seatbooker.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {
}