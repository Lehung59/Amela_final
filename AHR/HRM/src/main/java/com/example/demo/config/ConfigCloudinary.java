package com.example.demo.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloudinary {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dqvr7kat6");
        config.put("api_key", "712758645674169");
        config.put("api_secret", "CN0CG-NGsuJIpwn2UdLCBvX72FE");
        config.put("secure", "true");

        return new Cloudinary(config);
    }


}
