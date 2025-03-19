package com.example.seatbooker.controller;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.Seat;
import com.example.seatbooker.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;
    
    @GetMapping("/room/{roomId}")
    public Result<List<Seat>> listSeatsByRoom(@PathVariable Long roomId) {
        return seatService.listSeatsByRoom(roomId);
    }
    
    @GetMapping("/{id}")
    public Result<Seat> getSeatDetail(@PathVariable Long id) {
        return seatService.getSeatDetail(id);
    }
    
    @GetMapping("/available")
    public Result<List<Seat>> listAvailableSeats(
            @RequestParam Long roomId, 
            @RequestParam String date, 
            @RequestParam Integer hour) {
        return seatService.listAvailableSeats(roomId, date, hour);
    }
} 