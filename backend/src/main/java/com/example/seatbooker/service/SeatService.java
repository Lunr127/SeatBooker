package com.example.seatbooker.service;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.Seat;

import java.util.List;

public interface SeatService {
    
    Result<List<Seat>> listSeatsByRoom(Long roomId);
    
    Result<Seat> getSeatDetail(Long seatId);
    
    Result<List<Seat>> listAvailableSeats(Long roomId, String date, Integer hour);
} 