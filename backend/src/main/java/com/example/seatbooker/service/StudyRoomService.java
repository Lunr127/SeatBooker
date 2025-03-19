package com.example.seatbooker.service;

import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.StudyRoom;

import java.util.List;

public interface StudyRoomService {
    
    Result<List<StudyRoom>> listRooms(Long departmentId);
    
    Result<StudyRoom> getRoomDetail(Long roomId);
    
    Result<String> generateRoomQrCode(Long roomId);
} 