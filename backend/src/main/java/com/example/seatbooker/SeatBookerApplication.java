package com.example.seatbooker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.seatbooker.mapper")
@EnableScheduling
public class SeatBookerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatBookerApplication.class, args);
    }
} 