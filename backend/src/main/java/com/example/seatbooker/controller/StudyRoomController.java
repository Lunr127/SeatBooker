package com.example.seatbooker.controller;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.StudyRoom;
import com.example.seatbooker.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/rooms")
public class StudyRoomController {

    @Autowired
    private StudyRoomService studyRoomService;
    
    @GetMapping
    public Result<List<StudyRoom>> listRooms(@RequestParam(required = false) Long departmentId) {
        return studyRoomService.listRooms(departmentId);
    }
    
    @GetMapping("/{id}")
    public Result<StudyRoom> getRoomDetail(@PathVariable Long id) {
        return studyRoomService.getRoomDetail(id);
    }
    
    @GetMapping("/{id}/qrcode")
    public Result<String> getRoomQrCode(@PathVariable Long id) {
        return studyRoomService.generateRoomQrCode(id);
    }
} 