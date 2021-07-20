package au.com.flexisoft.redis;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultiThreadService {

    @Autowired
    RedisMultiThreadTransactionRetryTest redisMultiThreadTransactionRetryTest;

    public CompletableFuture callRedisMethods(Account cashAccount, Account creditAccount) throws InterruptedException {
        System.out.println("calling callRedisMethods");

        redisMultiThreadTransactionRetryTest.populateRedis(cashAccount, creditAccount);

        return CompletableFuture.completedFuture(null);
    }

}
