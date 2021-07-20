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

    public static final String ACCOUNT_MAIN_KEY = "ACCOUNT";
    private final RedisCacheHashOperation hashOperation;

    public RedisTransaction(RedisCacheHashOperation hashOperation) {
        this.hashOperation = hashOperation;
    }


    public void inTransaction(Integer time, String methodCall) {

        Account cash1 = Account.builder().type("cash").id(1).key("1").amount(1.1).build();
        Account cashAcc = (Account) hashOperation.getValue(ACCOUNT_MAIN_KEY, cash1.getKey());

        //execute a transaction
        List<Object> txResults = (List<Object>) hashOperation.getRedisTemplate().execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().type("cash").key("1").id(1).amount(1.1).build();
                Account credit = Account.builder().type("credit").key("2").id(2).amount(2.2).build();

                operations.watch("1"); // THIS DOES NOT WORK. throw new UnsupportedOperationException(); It is in LettuceConnection

                operations.multi();
                HashOperations hashOperations = operations.opsForHash();
                /*hashOperations.put(ACCOUNT_MAIN_KEY, cash.getKey(), cash);
                hashOperations.put(ACCOUNT_MAIN_KEY, credit.getKey(), credit);*/

//                cash = Account.builder().key("cash").type("cash").id(1).amount(3.3).build();
//                credit = Account.builder().key("credit").type("credit").id(2).amount(4.4).build();


//

                //TODO: SOME REASON THIS DOESN'T READ HERE. IT RETURNS NULL HERE. SO HAVE TO READ BEFORE IT ENTERS HERE.
                // THIS ACTUALLY RETURNS BUT EXEC CALL IS AT THE END, HENCE IT DOESN'T SHOW THAT AT THIS POINT AND RETURNS NULL
                // SO IT WORKS AS EXPECTED.
                // WE HAVE TO RETRIEVE THE VALUE BEFORE AND ONLY UPDATE IT INSIDE
                Account cashAcc2 = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, cash1.getKey());
                cashAcc.setAmount(333.37);
                System.out.println("cashAcc-: "+cashAcc);
//                Account creditAcc = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, credit.getKey());
//                System.out.println("creditAcc-: "+creditAcc);

                try {
                    System.err.println("CALLING SLEEP");
                    Thread.sleep(time);
                    System.err.println("END OF SLEEP");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hashOperations.put(ACCOUNT_MAIN_KEY, cash.getKey(), cashAcc);

//                if (true) {
//                    throw new RuntimeException("error");
//                }

                /*hashOperations.put("ACCOUNT", cash.getKey(), cash);
                hashOperations.put("ACCOUNT", credit.getKey(), credit);*/

                // This will contain the results of all operations in the transaction

                return operations.exec();
            }
        });

        System.err.println("*********************Number of items added to set:::::::::"+methodCall);

        txResults.forEach(e -> System.out.println("E::"+e));

    }

    public void readTransaction() {
        Set account = hashOperation.getKeys(ACCOUNT_MAIN_KEY);
        account.forEach(System.out::println);

        Account cash = Account.builder().type("cash").id(1).key("1").amount(1.1).build();
        Account credit = Account.builder().type("credit").id(2).key("2").amount(2.2).build();

        Account cashAcc = (Account) hashOperation.getValue(ACCOUNT_MAIN_KEY, cash.getKey());
        Account creditAcc = (Account) hashOperation.getValue(ACCOUNT_MAIN_KEY, credit.getKey());

        System.out.println("cash:::: "+cashAcc);
        System.out.println("credit:::: "+creditAcc);
    }

}
