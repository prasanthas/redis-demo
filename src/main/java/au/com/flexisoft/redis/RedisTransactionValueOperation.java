package au.com.flexisoft.redis;

import java.util.Collections;
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
    public void transaction1(Integer time, String methodCall, Double amount, Double amount2) {
        System.err.println("transaction1 CALLED times: "+ ++transaction1);
        final String KEY = "1";
        final String KEY_2 = "2";

        List<Object> txResults = (List<Object>) redisValueOperationsCache.getRedisTemplate().execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(List.of(KEY,KEY_2));
                Account account = redisValueOperationsCache.getValue(KEY);
                Account account_2 = redisValueOperationsCache.getValue(KEY_2);
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
                account_2.setAmount(amount2);
                redisValueOperationsCache.put(KEY_2, account_2);

                List exec = operations.exec();

                if (CollectionUtils.isEmpty(exec)) {
                    System.err.println("****FAILED **************** 1:"+amount);
                    System.err.println("****FAILED **************** 2:"+amount2);
                    throw new RuntimeException("Didin't commit");
                } else {
                    System.err.println("****SUCCEEDED **************** 1:"+amount);
                    System.err.println("****SUCCEEDED **************** 2:"+amount2);
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

    public void pupulateRedis() {
        Account cash = Account.builder().type("cash").key("1").id(1).amount(1.1).build();
        Account cash_2 = Account.builder().type("cash-2").key("2").id(2).amount(2.2).build();

        redisValueOperationsCache.put("1",cash);
        redisValueOperationsCache.put("2",cash_2);
    }
}
