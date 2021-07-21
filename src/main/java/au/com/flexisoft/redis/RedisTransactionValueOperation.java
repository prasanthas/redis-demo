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

@Service
public class RedisTransactionValueOperation {

    private final RedisValueOperationsCache<Account> redisValueOperationsCache;

    private Integer transaction1 = 0;
    private Integer transaction2 = 0;

    public RedisTransactionValueOperation(RedisValueOperationsCache redisValueOperationsCache) {
        this.redisValueOperationsCache = redisValueOperationsCache;
    }

    @Retryable
    public void transaction1(Integer time, String methodCall) {
        System.err.println("transaction1 CALLED times: "+ ++transaction1);
        final String KEY = "1";

        List<Object> txResults = (List<Object>) redisValueOperationsCache.getRedisTemplate().execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().type("cash").key(KEY).id(1).amount(1.1).build();

                operations.watch(KEY);
                System.out.println("transaction1-Watch Called");
                operations.multi();
                ValueOperations<String, Account> valueOperations = operations.opsForValue();

                Account account = valueOperations.get(KEY);
                if (account == null) {
                    account = cash;
                }
                account.setAmount(1.11);

                try {
                    System.err.println("transaction1-CALLING SLEEP - 1");
                    Thread.sleep(time);
                    System.err.println("transaction1-END OF SLEEP - 1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                valueOperations.set(KEY, account, 1000, TimeUnit.MINUTES);

                List exec = operations.exec();
                System.out.println("transaction1 - End Of Execute:exec::" + exec);

                /*if (CollectionUtils.isEmpty(exec)) {
                    throw new RuntimeException("Didin't commit");
                }*/

                return exec;
            }
        });
        System.out.println("transaction1 -  txResult:"+txResults);
    }

    @Retryable
    public void transaction2(Integer time, String methodCall) {
        System.err.println("transaction2 CALLED times: "+ ++transaction2);
        final String KEY = "1";

        List<Object> txResults = (List<Object>) redisValueOperationsCache.getRedisTemplate().execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().type("cash").key(KEY).id(1).amount(1.1).build();

                operations.watch(KEY);
                System.out.println("transaction2-Watch Called");
                operations.multi();
                ValueOperations<String, Account> valueOperations = operations.opsForValue();

                Account account = valueOperations.get(KEY);
                if (account == null) {
                    account = cash;
                }
                account.setAmount(1.11);

                try {
                    System.err.println("transaction2-CALLING SLEEP - 1");
                    Thread.sleep(time);
                    System.err.println("transaction2-END OF SLEEP - 1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                valueOperations.set(KEY, account, 1000, TimeUnit.MINUTES);

                List exec = operations.exec();
                System.out.println("transaction2 - End Of Execute:exec:" + exec);
                /*if (CollectionUtils.isEmpty(exec)) {
                    throw new RuntimeException("Didin't commit");
                }*/
                return exec;
            }
        });
        System.out.println("transaction2 -  txResult:"+txResults);
    }

    public void readTransaction(String... keys) {

        Stream.of(keys).forEach(key -> {
            Account value = redisValueOperationsCache.getValue(key);
            System.out.println(value);
        });

    }
}
