package au.com.flexisoft.redis;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisValueOperationsCache<T> {

    @Resource(name="redisTemplate")
    private ValueOperations<String, T> valueOperations;

    @Autowired
    private RedisTemplate redisTemplate;

    public void putValueWithExpireTime(String key, T value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value, timeout, unit);
    }

    public void put(String key, T value) {
        valueOperations.set(key, value);
    }

    public void putValueWithExpireTime(String key, T value, Duration duration) {
        valueOperations.set(key, value, duration);
    }

    public T getValue(String key) {
        return valueOperations.get(key);
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}
