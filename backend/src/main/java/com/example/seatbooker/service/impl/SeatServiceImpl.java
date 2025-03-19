package com.example.seatbooker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.Reservation;
import com.example.seatbooker.entity.Seat;
import com.example.seatbooker.mapper.ReservationMapper;
import com.example.seatbooker.mapper.SeatMapper;
import com.example.seatbooker.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;
    
    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public Result<List<Seat>> listSeatsByRoom(Long roomId) {
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getRoomId, roomId);
        queryWrapper.eq(Seat::getStatus, 1); // 正常状态的座位
        queryWrapper.orderByAsc(Seat::getRowNumber).orderByAsc(Seat::getColumnNumber);
        
        List<Seat> seatList = seatMapper.selectList(queryWrapper);
        return Result.success(seatList);
    }

    @Override
    public Result<Seat> getSeatDetail(Long seatId) {
        Seat seat = seatMapper.selectById(seatId);
        if (seat == null) {
            return Result.fail("座位不存在");
        }
        return Result.success(seat);
    }

    @Override
    public Result<List<Seat>> listAvailableSeats(Long roomId, String date, Integer hour) {
        // 查询自习室的所有座位
        LambdaQueryWrapper<Seat> seatQueryWrapper = new LambdaQueryWrapper<>();
        seatQueryWrapper.eq(Seat::getRoomId, roomId);
        seatQueryWrapper.eq(Seat::getStatus, 1); // 正常状态的座位
        List<Seat> allSeats = seatMapper.selectList(seatQueryWrapper);
        
        if (allSeats.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        // 计算查询时间
        LocalDate queryDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        LocalDateTime startTime = queryDate.atTime(hour, 0);
        LocalDateTime endTime = startTime.plusHours(1);
        
        // 查询已预约的座位
        LambdaQueryWrapper<Reservation> reservationQueryWrapper = new LambdaQueryWrapper<>();
        reservationQueryWrapper.in(Reservation::getSeatId, allSeats.stream().map(Seat::getId).collect(Collectors.toList()));
        reservationQueryWrapper.in(Reservation::getStatus, 0, 1); // 未签到或已签到状态
        reservationQueryWrapper.and(wrapper -> 
            wrapper.and(w -> 
                w.le(Reservation::getStartTime, startTime)
                 .ge(Reservation::getEndTime, startTime)
            ).or(w -> 
                w.le(Reservation::getStartTime, endTime)
                 .ge(Reservation::getEndTime, endTime)
            ).or(w -> 
                w.ge(Reservation::getStartTime, startTime)
                 .le(Reservation::getEndTime, endTime)
            )
        );
        
        List<Reservation> reservedList = reservationMapper.selectList(reservationQueryWrapper);
        
        // 过滤出可用座位
        List<Long> reservedSeatIds = reservedList.stream().map(Reservation::getSeatId).collect(Collectors.toList());
        List<Seat> availableSeats = allSeats.stream()
                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
                .collect(Collectors.toList());
        
        return Result.success(availableSeats);
    }
} 