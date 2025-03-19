package com.example.seatbooker.controller;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.CheckInDTO;
import com.example.seatbooker.dto.ReservationDTO;
import com.example.seatbooker.entity.Reservation;
import com.example.seatbooker.service.ReservationService;
import com.example.seatbooker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public Result<Boolean> createReservation(@RequestBody @Validated ReservationDTO reservationDTO) {
        // 获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        return reservationService.createReservation(reservationDTO, userId);
    }
    
    @PostMapping("/check-in")
    public Result<Boolean> checkIn(@RequestBody @Validated CheckInDTO checkInDTO) {
        // 设置当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        checkInDTO.setUserId(userId);
        
        return reservationService.checkIn(checkInDTO);
    }
    
    @GetMapping
    public Result<List<Reservation>> getUserReservations(@RequestParam(required = false) Integer status) {
        // 获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        return reservationService.getUserReservations(userId, status);
    }
    
    @GetMapping("/{id}")
    public Result<Reservation> getReservationDetail(@PathVariable Long id) {
        return reservationService.getReservationDetail(id);
    }
    
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelReservation(@PathVariable Long id) {
        // 获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((String) authentication.getCredentials()).replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        
        return reservationService.cancelReservation(id, userId);
    }
} 