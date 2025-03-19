package com.example.seatbooker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seatbooker.entity.Seat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeatMapper extends BaseMapper<Seat> {
} 