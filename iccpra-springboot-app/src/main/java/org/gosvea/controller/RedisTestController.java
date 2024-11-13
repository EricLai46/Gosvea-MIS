package org.gosvea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = { "https://allcprmanage.com", "http://localhost:3000", "http://localhost:8080" }, allowedHeaders = "*")

public class RedisTestController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/testredis")
    public String testRedis() {
        // 写入数据
        redisTemplate.opsForValue().set("testKey", "Hello, Redis!");

        // 读取数据
        String value = (String) redisTemplate.opsForValue().get("testKey");
        return "Redis Value: " + value;
    }
}
