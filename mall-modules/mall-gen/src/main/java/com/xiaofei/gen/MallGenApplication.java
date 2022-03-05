package com.xiaofei.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.xiaofei.common.security.annotation.EnableCustomConfig;
import com.xiaofei.common.security.annotation.EnableRyFeignClients;
import com.xiaofei.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 代码生成
 *
 * @author 李飞
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringBootApplication
public class MallGenApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallGenApplication.class, args);
    }
}
