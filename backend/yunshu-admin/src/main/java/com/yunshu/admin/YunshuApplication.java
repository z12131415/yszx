package com.yunshu.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = "com.yunshu")
@MapperScan("com.yunshu.*.mapper")
public class YunshuApplication {
    public static void main(String[] args) {
        SpringApplication.run(YunshuApplication.class, args);
    }
}
