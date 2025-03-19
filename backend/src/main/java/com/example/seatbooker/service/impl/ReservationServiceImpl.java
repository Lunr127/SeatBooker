package com.example.seatbooker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.CheckInDTO;
import com.example.seatbooker.dto.ReservationDTO;
import com.example.seatbooker.entity.CheckIn;
import com.example.seatbooker.entity.Reservation;
import com.example.seatbooker.entity.Seat;
import com.example.seatbooker.entity.User;
import com.example.seatbooker.entity.Violation;
import com.example.seatbooker.mapper.CheckInMapper;
import com.example.seatbooker.mapper.ReservationMapper;
import com.example.seatbooker.mapper.SeatMapper;
import com.example.seatbooker.mapper.SystemParamMapper;
import com.example.seatbooker.mapper.UserMapper;
import com.example.seatbooker.mapper.ViolationMapper;
import com.example.seatbooker.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationMapper reservationMapper;
    
    @Autowired
    private SeatMapper seatMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CheckInMapper checkInMapper;
    
    @Autowired
    private ViolationMapper violationMapper;
    
    @Autowired
    private SystemParamMapper systemParamMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public Result<Boolean> createReservation(ReservationDTO reservationDTO, Long userId) {
        // 获取座位信息
        Seat seat = seatMapper.selectById(reservationDTO.getSeatId());
        if (seat == null) {
            return Result.fail("座位不存在");
        }
        
        // 检查座位状态
        if (seat.getStatus() != 1) {
            return Result.fail("座位不可用");
        }
        
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 检查用户违约次数
        int maxViolationCount = Integer.parseInt(systemParamMapper.getParamValue("max_violation_count"));
        if (user.getViolationCount() >= maxViolationCount) {
            return Result.fail("违约次数过多，暂时无法预约");
        }
        
        // 检查预约时间是否合理
        LocalDateTime now = LocalDateTime.now();
        if (reservationDTO.getStartTime().isBefore(now)) {
            return Result.fail("开始时间必须是未来时间");
        }
        
        if (reservationDTO.getEndTime().isBefore(reservationDTO.getStartTime())) {
            return Result.fail("结束时间必须晚于开始时间");
        }
        
        // 检查预约时长
        Duration duration = Duration.between(reservationDTO.getStartTime(), reservationDTO.getEndTime());
        long hours = duration.toHours();
        int maxBookingHours = Integer.parseInt(systemParamMapper.getParamValue("max_booking_hours"));
        if (hours > maxBookingHours) {
            return Result.fail("预约时长最多为" + maxBookingHours + "小时");
        }
        
        // 检查预约时间是否是整点
        if (reservationDTO.getStartTime().getMinute() != 0 || reservationDTO.getEndTime().getMinute() != 0) {
            return Result.fail("预约时间必须为整点");
        }
        
        // 检查是否有时间冲突
        String lockKey = "seat_lock:" + reservationDTO.getSeatId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            return Result.fail("座位正在被预约，请稍后再试");
        }
        
        try {
            Integer count = reservationMapper.checkSeatAvailable(
                    reservationDTO.getSeatId(),
                    reservationDTO.getStartTime().toString(),
                    reservationDTO.getEndTime().toString()
            );
            
            if (count > 0) {
                return Result.fail("该时间段座位已被预约");
            }
            
            // 创建预约记录
            Reservation reservation = new Reservation();
            reservation.setUserId(userId);
            reservation.setSeatId(reservationDTO.getSeatId());
            reservation.setStartTime(reservationDTO.getStartTime());
            reservation.setEndTime(reservationDTO.getEndTime());
            reservation.setStatus(0); // 未签到状态
            
            reservationMapper.insert(reservation);
            
            return Result.success(true);
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional
    public Result<Boolean> cancelReservation(Long reservationId, Long userId) {
        // 查询预约记录
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.fail("预约记录不存在");
        }
        
        // 检查是否是用户自己的预约
        if (!reservation.getUserId().equals(userId)) {
            return Result.fail("无权取消他人预约");
        }
        
        // 检查预约状态
        if (reservation.getStatus() != 0) {
            return Result.fail("只能取消未签到状态的预约");
        }
        
        // 取消预约
        reservation.setStatus(3); // 已取消状态
        reservationMapper.updateById(reservation);
        
        return Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> checkIn(CheckInDTO checkInDTO) {
        // 验证房间验证码
        String storedCode = redisTemplate.opsForValue().get("room_code:" + checkInDTO.getRoomId());
        if (storedCode == null || !storedCode.equals(checkInDTO.getCode())) {
            return Result.fail("验证码错误或已过期");
        }
        
        // 查询用户当前预约
        Reservation reservation = reservationMapper.findCurrentReservation(checkInDTO.getUserId(), checkInDTO.getSeatId());
        if (reservation == null) {
            return Result.fail("没有找到有效预约");
        }
        
        // 已经签到过
        if (reservation.getStatus() == 1) {
            return Result.fail("已签到，无需重复签到");
        }
        
        // 更新预约状态为已签到
        reservation.setStatus(1);
        reservationMapper.updateById(reservation);
        
        // 创建签到记录
        CheckIn checkIn = new CheckIn();
        checkIn.setReservationId(reservation.getId());
        checkIn.setUserId(checkInDTO.getUserId());
        checkIn.setCheckInTime(LocalDateTime.now());
        
        checkInMapper.insert(checkIn);
        
        return Result.success(true);
    }

    @Override
    public Result<List<Reservation>> getUserReservations(Long userId, Integer status) {
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getUserId, userId);
        if (status != null) {
            queryWrapper.eq(Reservation::getStatus, status);
        }
        queryWrapper.orderByDesc(Reservation::getCreatedTime);
        
        List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
        return Result.success(reservations);
    }

    @Override
    public Result<Reservation> getReservationDetail(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            return Result.fail("预约记录不存在");
        }
        return Result.success(reservation);
    }
    
    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @Transactional
    public void handleTimeoutReservations() {
        // 获取超时时间
        int checkInTimeout = Integer.parseInt(systemParamMapper.getParamValue("check_in_timeout"));
        
        // 查询所有超时未签到的预约
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(checkInTimeout);
        
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getStatus, 0); // 未签到状态
        queryWrapper.le(Reservation::getStartTime, timeoutThreshold); // 开始时间早于超时阈值
        
        List<Reservation> timeoutReservations = reservationMapper.selectList(queryWrapper);
        
        for (Reservation reservation : timeoutReservations) {
            // 更新预约状态为违约
            reservation.setStatus(4); // 违约状态
            reservationMapper.updateById(reservation);
            
            // 记录违约
            Violation violation = new Violation();
            violation.setUserId(reservation.getUserId());
            violation.setReservationId(reservation.getId());
            violation.setViolationTime(LocalDateTime.now());
            violation.setReason("超时未签到");
            
            violationMapper.insert(violation);
            
            // 更新用户违约次数
            User user = userMapper.selectById(reservation.getUserId());
            user.setViolationCount(user.getViolationCount() + 1);
            userMapper.updateById(user);
        }
    }
    
    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void sendReservationReminders() {
        // 获取提前提醒时间
        int advanceReminder = Integer.parseInt(systemParamMapper.getParamValue("advance_reminder"));
        
        // 查询即将开始的预约
        LocalDateTime reminderThreshold = LocalDateTime.now().plusMinutes(advanceReminder);
        
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getStatus, 0); // 未签到状态
        queryWrapper.ge(Reservation::getStartTime, LocalDateTime.now());
        queryWrapper.le(Reservation::getStartTime, reminderThreshold);
        
        List<Reservation> upcomingReservations = reservationMapper.selectList(queryWrapper);
        
        for (Reservation reservation : upcomingReservations) {
            // 发送提醒通知，实际项目中应使用微信推送、短信或邮件等
            System.out.println("向用户ID" + reservation.getUserId() + "发送预约提醒，预约ID：" + reservation.getId());
            
            // 标记已提醒，避免重复提醒，可以用Redis记录
            String reminderKey = "reservation_reminder:" + reservation.getId();
            redisTemplate.opsForValue().set(reminderKey, "1", 1, TimeUnit.HOURS);
        }
        
        // 还可以实现超时未签到的二次提醒
        int checkInTimeout = Integer.parseInt(systemParamMapper.getParamValue("check_in_timeout"));
        
        LambdaQueryWrapper<Reservation> timeoutQueryWrapper = new LambdaQueryWrapper<>();
        timeoutQueryWrapper.eq(Reservation::getStatus, 0); // 未签到状态
        timeoutQueryWrapper.le(Reservation::getStartTime, LocalDateTime.now()); // 已开始
        timeoutQueryWrapper.ge(Reservation::getStartTime, LocalDateTime.now().minusMinutes(checkInTimeout)); // 未超时
        
        List<Reservation> needReminderList = reservationMapper.selectList(timeoutQueryWrapper);
        
        for (Reservation reservation : needReminderList) {
            // 检查是否已经发送过提醒
            String reminderKey = "reservation_timeout_reminder:" + reservation.getId();
            Boolean isSent = redisTemplate.hasKey(reminderKey);
            
            if (Boolean.FALSE.equals(isSent)) {
                // 发送超时提醒
                System.out.println("向用户ID" + reservation.getUserId() + "发送即将超时提醒，预约ID：" + reservation.getId());
                
                // 标记已提醒
                redisTemplate.opsForValue().set(reminderKey, "1", 1, TimeUnit.HOURS);
            }
        }
    }
} 