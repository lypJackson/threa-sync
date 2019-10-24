package com.alfred.datasync;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.alfred.datasync.mapper")
@SpringBootApplication
public class ThreasyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreasyncApplication.class, args);
    }

}
