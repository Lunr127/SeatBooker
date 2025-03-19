package com.example.seatbooker.service;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.CheckInDTO;
import com.example.seatbooker.dto.ReservationDTO;
import com.example.seatbooker.entity.Reservation;

import java.util.List;

public interface ReservationService {
    
    Result<Boolean> createReservation(ReservationDTO reservationDTO, Long userId);
    
    Result<Boolean> cancelReservation(Long reservationId, Long userId);
    
    Result<Boolean> checkIn(CheckInDTO checkInDTO);
    
    Result<List<Reservation>> getUserReservations(Long userId, Integer status);
    
    Result<Reservation> getReservationDetail(Long reservationId);
    
    void handleTimeoutReservations();
    
    void sendReservationReminders();
} 