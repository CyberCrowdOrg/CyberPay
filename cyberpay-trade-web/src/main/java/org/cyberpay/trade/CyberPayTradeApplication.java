package org.cyberpay.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@EnableCaching
@MapperScan("org.cyberpay.*.mapper")
public class CyberPayTradeApplication {

    private static Logger logger = LoggerFactory.getLogger(CyberPayTradeApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CyberPayTradeApplication.class);
        logger.info("........CyberPayTradeApplication Run........");
    }
}
