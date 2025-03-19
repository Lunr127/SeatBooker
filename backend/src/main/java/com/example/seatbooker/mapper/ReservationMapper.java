package com.example.seatbooker.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.seatbooker.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    /**
     * 查询用户当前有效预约
     */
    @Select("SELECT * FROM reservation WHERE user_id = #{userId} AND seat_id = #{seatId} " +
            "AND status = 0 AND start_time <= NOW() AND end_time >= NOW() LIMIT 1")
    Reservation findCurrentReservation(@Param("userId") Long userId, @Param("seatId") Long seatId);
    
    /**
     * 检查座位在时间段内是否已被预约
     */
    @Select("SELECT COUNT(*) FROM reservation WHERE seat_id = #{seatId} " +
            "AND status IN (0, 1) " +
            "AND ((start_time <= #{startTime} AND end_time >= #{startTime}) " +
            "OR (start_time <= #{endTime} AND end_time >= #{endTime}) " +
            "OR (start_time >= #{startTime} AND end_time <= #{endTime}))")
    Integer checkSeatAvailable(@Param("seatId") Long seatId, 
                              @Param("startTime") String startTime, 
                              @Param("endTime") String endTime);
} 