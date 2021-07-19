package au.com.flexisoft.redis;

import java.util.Set;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedisSpringTransaction {

    private final RedisCacheHashOperation hashOperation;

    public RedisSpringTransaction(RedisCacheHashOperation hashOperation) {
        this.hashOperation = hashOperation;
    }

    private Integer numberOfRetries = 0;

//    @Retryable(RuntimeException.class)
    @Retryable
    @Transactional
    public void performTransactional() {
        Account cashAccount = Account.builder().id(1).amount(1.2).type("Cash").key("1").build();
        Account creditAccount = Account.builder().id(2).amount(2.3).type("Credit").key("2").build();

        System.err.println("About to add to cache: "+ numberOfRetries++);
        hashOperation.putValue(cashAccount.getType(), cashAccount.getKey(), cashAccount);
        hashOperation.putValue(creditAccount.getType(), creditAccount.getKey(), creditAccount);
        System.out.println("Added to cache");

        Set keys = hashOperation.getKeys("*");
        System.out.println("Keys: "+keys);
        System.out.println("Keys Size: "+keys.size());
        Account cashAccReturned = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());
        System.out.println("Returned Cash Account: "+cashAccReturned);

        cashAccReturned.setAmount(3.1115);
        System.out.println("cashAccReturned.setAmount(1.111);");

        hashOperation.putValue(cashAccReturned.getType(), cashAccReturned.getKey(), cashAccReturned);

        Account creditAccReturned = (Account) hashOperation.getValue(creditAccount.getType(), creditAccount.getKey());
        System.out.println("Returned Credit Accoutn: "+creditAccReturned);

        creditAccReturned.setAmount(4.2223);
        System.out.println("creditAccReturned.setAmount(2.222);");

        /*try {
            System.err.println("Entering Sleep");
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("Waking up from Sleep");*/
        if (true) {
            throw new RuntimeException("SOmething wrong");
        }

        hashOperation.putValue(creditAccReturned.getType(), creditAccReturned.getKey(), creditAccReturned);

        System.out.println("Final Read and see values");
        Account cashAccReturned2 = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());
        Account creditAccReturned2 = (Account) hashOperation.getValue(creditAccount.getType(), creditAccount.getKey());

        System.out.println("cashAccReturned2: "+cashAccReturned2);
        System.out.println("creditAccReturned2: "+creditAccReturned2);

    }

    public void readFromCache() {
        Account cashAccount = Account.builder().id(1).amount(1.1).type("Cash").key("1").build();
        Account creditAccount = Account.builder().id(2).amount(2.2).type("Credit").key("2").build();

        System.err.println("************READ EMPLOYEE CACHE***********************: ");
        Set employeeKeys = hashOperation.getKeys(Employee.HASH_KEY);
        employeeKeys.forEach(System.out::println);
        System.err.println("************ END READ EMPLOYEE CACHE***********************");

        System.out.println("************READ ACCOUNT CACHE***********************");
        Set cashAccountKeys = hashOperation.getKeys(cashAccount.getType());
        cashAccountKeys.forEach(System.out::println);

        Account account = (Account) hashOperation.getValue(cashAccount.getType(), cashAccount.getKey());
        System.out.println(account);

        Set creditAccountKeys = hashOperation.getKeys(creditAccount.getType());
        creditAccountKeys.forEach(System.out::println);

        account = (Account) hashOperation.getValue(creditAccount.getType(), creditAccount.getKey());
        System.out.println(account);

        System.out.println("************END READ ACCOUNT CACHE***********************");
    }


}
