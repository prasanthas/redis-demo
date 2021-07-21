package au.com.flexisoft.redis;

import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RedisTransaction {

    public static final String ACCOUNT_MAIN_KEY = "ACCOUNT";
    private final RedisCacheHashOperation hashOperation;

    public RedisTransaction(RedisCacheHashOperation hashOperation) {
        this.hashOperation = hashOperation;
    }

    private Integer inTransaction = 0;
    private Integer inTransactionCall2 = 0;

    @Retryable
    public void inTransaction(Integer time, String methodCall) {
        System.err.println("inTransaction CALLED:: "+inTransaction++);
        Account cash1 = Account.builder().type("cash").id(1).key("1").amount(1.1).build();
        Account cashAcc = (Account) hashOperation.getValue(ACCOUNT_MAIN_KEY, cash1.getKey());

        //execute a transaction
        List<Object> txResults = (List<Object>) hashOperation.getRedisTemplate().execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().type("cash").key("1").id(1).amount(1.1).build();
                Account credit = Account.builder().type("credit").key("2").id(2).amount(2.2).build();

//                operations.watch(cash.getKey());
                operations.watch(ACCOUNT_MAIN_KEY);
                System.out.println("Watch called 1: "+cash.getKey());

                operations.multi();
                HashOperations hashOperations = operations.opsForHash();


                //TODO: SOME REASON THIS DOESN'T READ HERE. IT RETURNS NULL HERE. SO HAVE TO READ BEFORE IT ENTERS HERE.
                // THIS ACTUALLY RETURNS BUT EXEC CALL IS AT THE END, HENCE IT DOESN'T SHOW THAT AT THIS POINT AND RETURNS NULL
                // SO IT WORKS AS EXPECTED.
                // WE HAVE TO RETRIEVE THE VALUE BEFORE AND ONLY UPDATE IT INSIDE
                // THE WAY IT SHOULD WORK IS, READ OUTSIDE MULTI AND ONLY UPDATE INSIDE MULTI.
                // NEXT TEST OUT THE DIRTY READ
                Account cashAcc2 = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, cash1.getKey());
                cashAcc.setAmount(333.38);
                System.out.println("cashAcc-: "+cashAcc);
//                Account creditAcc = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, credit.getKey());
//                System.out.println("creditAcc-: "+creditAcc);

                try {
                    System.err.println("CALLING SLEEP - 1");
                    Thread.sleep(time);
                    System.err.println("END OF SLEEP - 1");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hashOperations.put(ACCOUNT_MAIN_KEY, cash.getKey(), cashAcc);
                hashOperations.put(ACCOUNT_MAIN_KEY, "111", cashAcc);

//                if (true) {
//                    throw new RuntimeException("error");
//                }

                /*hashOperations.put("ACCOUNT", cash.getKey(), cash);
                hashOperations.put("ACCOUNT", credit.getKey(), credit);*/

                // This will contain the results of all operations in the transaction

                List exec = operations.exec();
                System.out.println("EXECUTE CALLED -1 exec::"+exec);
                exec.forEach(e-> System.out.println("EEE::: -1 "+e));

                return exec;
            }
        });

        System.err.println("*********************Number of items added to set::::::::: - 1"+methodCall+"::txResults::"+txResults);

        txResults.forEach(e -> System.out.println("RESULTS:: - 1-"+e));

        if (CollectionUtils.isEmpty(txResults)) {
            System.err.println("EMPTY RESULTS");
            throw new RuntimeException("EMPTY RESULTS");
        }
    }

    @Retryable
    public void inTransactionCall2(Integer time, String methodCall) {
        System.err.println("inTransactionCall2 CALLED:: "+inTransactionCall2++);
        Account cash1 = Account.builder().type("cash").id(1).key("1").amount(1.1).build();
        Account cashAcc = (Account) hashOperation.getValue(ACCOUNT_MAIN_KEY, cash1.getKey());

        //execute a transaction
        List<Object> txResults = (List<Object>) hashOperation.getRedisTemplate().execute(new SessionCallback<List<Object>>() {
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                Account cash = Account.builder().type("cash").key("1").id(1).amount(1.1).build();
                Account credit = Account.builder().type("credit").key("2").id(2).amount(2.2).build();

//                operations.watch(cash.getKey());
                operations.watch(ACCOUNT_MAIN_KEY);
                System.out.println("Watch called 2: "+cash.getKey());

                operations.multi();
                HashOperations hashOperations = operations.opsForHash();


                //TODO: SOME REASON THIS DOESN'T READ HERE. IT RETURNS NULL HERE. SO HAVE TO READ BEFORE IT ENTERS HERE.
                // THIS ACTUALLY RETURNS BUT EXEC CALL IS AT THE END, HENCE IT DOESN'T SHOW THAT AT THIS POINT AND RETURNS NULL
                // SO IT WORKS AS EXPECTED.
                // WE HAVE TO RETRIEVE THE VALUE BEFORE AND ONLY UPDATE IT INSIDE
                // THE WAY IT SHOULD WORK IS, READ OUTSIDE MULTI AND ONLY UPDATE INSIDE MULTI.
                // NEXT TEST OUT THE DIRTY READ
                Account cashAcc2 = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, cash1.getKey());
                cashAcc.setAmount(333.38);
                System.out.println("cashAcc-: "+cashAcc);
//                Account creditAcc = (Account) hashOperations.get(ACCOUNT_MAIN_KEY, credit.getKey());
//                System.out.println("creditAcc-: "+creditAcc);

                try {
                    System.err.println("CALLING SLEEP - 2");
                    Thread.sleep(time);
                    System.err.println("END OF SLEEP - 2");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hashOperations.put(ACCOUNT_MAIN_KEY, cash.getKey(), cashAcc);
                hashOperations.put(ACCOUNT_MAIN_KEY, "111", cashAcc);

//                if (true) {
//                    throw new RuntimeException("error");
//                }

                /*hashOperations.put("ACCOUNT", cash.getKey(), cash);
                hashOperations.put("ACCOUNT", credit.getKey(), credit);*/

                // This will contain the results of all operations in the transaction
                List exec = operations.exec();
                System.out.println("EXECUTE CALLED -2 exec::"+exec);
                exec.forEach(e-> System.out.println("EEE::: -2 "+e));

                return exec;
            }
        });

        System.err.println("*********************Number of items added to set::::::::: - 2"+methodCall+ "::txResults::"+txResults);

        txResults.forEach(e -> System.out.println("RESULTS:: - 2-"+e));

        if (CollectionUtils.isEmpty(txResults)) {
            System.err.println("EMPTY RESULTS");
            throw new RuntimeException("EMPTY RESULTS");
        }
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
