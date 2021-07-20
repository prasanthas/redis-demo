package au.com.flexisoft.redis;

import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class RedisTransaction {

    private final RedisCacheHashOperation hashOperation;

    public RedisTransaction(RedisCacheHashOperation hashOperation) {
        this.hashOperation = hashOperation;
    }


    public void inTransaction(Integer time, String methodCall) {

        //execute a transaction
        List<Object> txResults = (List<Object>) hashOperation.getRedisTemplate().execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().key("cash").type("cash").id(1).amount(1.2).build();
                Account credit = Account.builder().key("credit").type("credit").id(2).amount(2.2).build();

                operations.multi();
//                operations.watch("cash"); // THIS DOES NOT WORK. throw new UnsupportedOperationException(); It is in LettuceConnection
                HashOperations hashOperations = operations.opsForHash();
                hashOperations.put("ACCOUNT", cash.getKey(), cash);
                hashOperations.put("ACCOUNT", credit.getKey(), credit);

                cash = Account.builder().key("cash").type("cash").id(1).amount(3.3).build();
                credit = Account.builder().key("credit").type("credit").id(2).amount(4.4).build();

                try {
                    System.err.println("CALLING SLEEP");
                    Thread.sleep(time);
                    System.err.println("END OF SLEEP");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                if (true) {
//                    throw new RuntimeException("error");
//                }

                hashOperations.put("ACCOUNT", cash.getKey(), cash);
                hashOperations.put("ACCOUNT", credit.getKey(), credit);

                // This will contain the results of all operations in the transaction
                return operations.exec();
            }
        });

        System.err.println("*********************Number of items added to set: " + txResults.get(0)+ "::::::::"+methodCall);
        txResults.forEach(System.out::println);

    }

    public void readTransaction() {
        Set account = hashOperation.getKeys("ACCOUNT");
        account.forEach(System.out::println);

        Account cash = Account.builder().key("cash").type("cash").id(1).amount(1.1).build();
        Account credit = Account.builder().key("credit").type("credit").id(2).amount(2.2).build();

        Account cashAcc = (Account) hashOperation.getValue("ACCOUNT", cash.getKey());
        Account creditAcc = (Account) hashOperation.getValue("ACCOUNT", credit.getKey());

        System.out.println("cash: "+cashAcc);
        System.out.println("credit: "+creditAcc);
    }

}
