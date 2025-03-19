package com.example.seatbooker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.seatbooker.common.Result;
import com.example.seatbooker.dto.UserLoginDTO;
import com.example.seatbooker.dto.UserRegisterDTO;
import com.example.seatbooker.entity.User;
import com.example.seatbooker.mapper.UserMapper;
import com.example.seatbooker.service.UserService;
import com.example.seatbooker.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public Result<String> register(UserRegisterDTO registerDTO) {
        // 检查学号是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStudentId, registerDTO.getStudentId());
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return Result.fail("该学号已注册");
        }
        
        // 创建用户对象
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 密码加密
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        
        // 设置初始信用分和违约次数
        user.setCreditScore(100);
        user.setViolationCount(0);
        user.setStatus(1);
        
        // 保存用户
        userMapper.insert(user);
        
        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(UserLoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStudentId, loginDTO.getStudentId());
        User user = userMapper.selectOne(queryWrapper);
        
        // 判断用户是否存在
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 判断用户状态
        if (user.getStatus() != 1) {
            return Result.fail("账号已被禁用");
        }
        
        // 判断密码是否正确
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail("密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getStudentId(), user.getId());
        
        return Result.success("登录成功", token);
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result<Boolean> updateUserInfo(User user) {
        // 防止修改关键字段
        User dbUser = userMapper.selectById(user.getId());
        if (dbUser == null) {
            return Result.fail("用户不存在");
        }
        
        // 只允许修改昵称、手机号、邮箱
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setNickname(user.getNickname());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        
        userMapper.updateById(updateUser);
        
        return Result.success(true);
    }
} 