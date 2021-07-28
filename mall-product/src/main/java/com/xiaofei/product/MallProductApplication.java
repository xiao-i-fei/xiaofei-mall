package com.xiaofei.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * User: 李飞
 * Date: 2021/7/23
 * Time: 21:46
 */
@EnableTransactionManagement//开始事务管理，开启之后，只要在方法上标注@Transaction即可使用注解
@EnableDiscoveryClient //将服务注册到nacos中,需要设置spring.application.name的值
@SpringBootApplication
public class MallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class, args);
    }
}
