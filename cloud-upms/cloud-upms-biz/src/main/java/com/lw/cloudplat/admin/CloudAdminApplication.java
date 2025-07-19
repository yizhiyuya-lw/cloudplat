package com.lw.cloudplat.admin;

import com.lw.cloudplat.common.feign.annotation.EnableCloudFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lw
 * @create 2025-07-19-22:50
 */
@EnableCloudFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class CloudAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudAdminApplication.class, args);
    }
}
