package org.gosvea.service.impl;

import org.gosvea.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired

    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void saveData(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            // 处理或记录异常
            System.err.println("保存数据到 Redis 时出错: " + e.getMessage());
        }
    }

    @Override
    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
