package com.lacus.admin;

import cn.hutool.core.date.DateUtil;
import com.lacus.datasource.manager.DataSourcePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.lacus.*")
@EnableFeignClients(basePackages = {"com.lacus.domain.oneapi.feign"})
@Slf4j
public class LacusApplication {

    @Autowired
    private DataSourcePluginManager dataSourcePluginManager;

    public static void main(String[] args) {
        SpringApplication.run(LacusApplication.class, args);
        System.out.println("Lacus大数据平台启动成功：" + DateUtil.now());
    }

    @EventListener
    public void run(ApplicationReadyEvent event) {
        log.info("开始注册数据源...");
        dataSourcePluginManager.registerAll();
    }
}
