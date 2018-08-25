package com.gaofeng;

import com.gaofeng.prisonim.InitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: gaofeng
 * @Date: 2018-08-25
 * @Description:
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        InitConfig.init();
        SpringApplication.run(Application.class, args);
    }
}
