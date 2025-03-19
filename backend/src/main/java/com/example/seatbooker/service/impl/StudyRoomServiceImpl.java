package com.example.seatbooker.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.seatbooker.common.Result;
import com.example.seatbooker.entity.StudyRoom;
import com.example.seatbooker.mapper.StudyRoomMapper;
import com.example.seatbooker.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StudyRoomServiceImpl implements StudyRoomService {

    @Autowired
    private StudyRoomMapper studyRoomMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Result<List<StudyRoom>> listRooms(Long departmentId) {
        LambdaQueryWrapper<StudyRoom> queryWrapper = new LambdaQueryWrapper<>();
        // 状态为开放的
        queryWrapper.eq(StudyRoom::getStatus, 1);
        
        // 如果有院系ID，则筛选特定院系的自习室或公共自习室
        if (departmentId != null) {
            queryWrapper.and(wrapper -> 
                wrapper.eq(StudyRoom::getDepartmentId, departmentId)
                      .or()
                      .isNull(StudyRoom::getDepartmentId)
            );
        }
        
        queryWrapper.orderByAsc(StudyRoom::getLocation);
        
        List<StudyRoom> roomList = studyRoomMapper.selectList(queryWrapper);
        return Result.success(roomList);
    }

    @Override
    public Result<StudyRoom> getRoomDetail(Long roomId) {
        StudyRoom room = studyRoomMapper.selectById(roomId);
        if (room == null) {
            return Result.fail("自习室不存在");
        }
        return Result.success(room);
    }

    @Override
    public Result<String> generateRoomQrCode(Long roomId) {
        // 查询自习室是否存在
        StudyRoom room = studyRoomMapper.selectById(roomId);
        if (room == null) {
            return Result.fail("自习室不存在");
        }
        
        // 生成随机验证码
        String code = RandomUtil.randomString(6);
        
        // 存入Redis，30分钟有效
        String key = "room_code:" + roomId;
        redisTemplate.opsForValue().set(key, code, 30, TimeUnit.MINUTES);
        
        return Result.success(code);
    }
}