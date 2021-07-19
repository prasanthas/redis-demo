package au.com.flexisoft.redis;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisCacheHashOperation<T> {

    @Resource(name="redisTemplate")
    private HashOperations<String, String, T> hashOperations;

    @Autowired
    private RedisTemplate redisTemplate;

    public void putValue(String hashKey, String key, T value) {
        hashOperations.put(hashKey,key,value);
    }

    public void setExpiry(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public T getValue(String hashKey, String key) {
        return hashOperations.get(hashKey,key);
    }

    public void delete(String key, String... hashKeys) {
        hashOperations.delete(key, hashKeys);
    }

    public Set<String> getKeys(String keyPattern) {
        return hashOperations.keys(keyPattern);
    }

    public Map<String, T> findAll(String hashKey) {
        return hashOperations.entries(hashKey);
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}

