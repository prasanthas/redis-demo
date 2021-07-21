package au.com.flexisoft.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RedisTransactionValueOperation {

    private final RedisValueOperationsCache<Account> redisValueOperationsCache;

    private Integer transaction1 = 0;
    private Integer transaction2 = 0;

    public RedisTransactionValueOperation(RedisValueOperationsCache redisValueOperationsCache) {
        this.redisValueOperationsCache = redisValueOperationsCache;
    }

    @Retryable
    public void transaction1(Integer time, String methodCall, Double amount) {
        System.err.println("transaction1 CALLED times: "+ ++transaction1);
        final String KEY = "1";

        List<Object> txResults = (List<Object>) redisValueOperationsCache.getRedisTemplate().execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(KEY);
                Account account = redisValueOperationsCache.getValue(KEY);
                System.out.println("transaction1-Watch Called");
                operations.multi();
                System.out.println("transaction1 - RETRIEVED account: "+account);
                try {
                    System.err.println("transaction1-CALLING SLEEP - 1");
                    Thread.sleep(time);
                    System.err.println("transaction1-END OF SLEEP - 1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                account.setAmount(amount);
                redisValueOperationsCache.put(KEY, account);

                List exec = operations.exec();

                if (CollectionUtils.isEmpty(exec)) {
                    System.err.println("****FAILED ****************:"+amount);
                    throw new RuntimeException("Didin't commit");
                } else {
                    System.err.println("****SUCCEEDED ****************:"+amount);
                }

                return exec;
            }
        });
        System.out.println("transaction1 -  txResult:"+txResults);
    }

    public void readTransaction(String... keys) {

        Stream.of(keys).forEach(key -> {
            Account value = redisValueOperationsCache.getValue(key);
            System.out.println("RRRR:::"+value);
        });

    }
}
