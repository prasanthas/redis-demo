package au.com.flexisoft.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisTransactionThread implements CommandLineRunner {

	@Autowired
	private RedisMultiThreadTransactionRetryTest redisMultiThreadTransactionRetryTest;

	@Autowired
	private RedisTransaction redisTransaction;

	public static void main(String[] args) {
		System.out.println("Redis Demo");
		SpringApplication.run(RedisTransactionThread.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println("Inside run");

//		run1();
		run2();
	}

	private void run1() throws InterruptedException, ExecutionException {
		Account cashAccount = Account.builder().id(1).amount(11.1223).type("Cash").key("1").build();
		Account creditAccount = Account.builder().id(2).amount(22.3333).type("Credit").key("2").build();

		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			try {
				System.out.println("***********FIRST THREAD CALLED*********");
				redisMultiThreadTransactionRetryTest.populateRedis(cashAccount, creditAccount);
				redisMultiThreadTransactionRetryTest.readRedis("FIRST THREAD");
				System.out.println("***********FIRST THREAD ENDED*********");
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
			System.out.println("I'll run in a separate thread than the main thread. - 1");
		});

		TimeUnit.SECONDS.sleep(1);

		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
			System.out.println("***********SECOND THREAD CALLED*********");
			redisMultiThreadTransactionRetryTest.populateRedisSecondCall(cashAccount, creditAccount);
			redisMultiThreadTransactionRetryTest.readRedis("SECOND THREAD");
			System.out.println("***********SECOND THREAD ENDED*********");
			System.out.println("I'll run in a separate thread than the main thread. - 2");
		});

		TimeUnit.SECONDS.sleep(5);

		CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
			System.out.println("***********THIRD THREAD CALLED*********");
			redisMultiThreadTransactionRetryTest.readRedis("THIRD THREAD");
			System.out.println("***********THIRD THREAD ENDED*********");
			System.out.println("I'll run in a separate thread than the main thread. - 2");
		});

		future2.get();
		future3.get();
		future1.get();

		System.err.println("*******FINAL OUTPUT ***************");
		redisMultiThreadTransactionRetryTest.readRedis();
	}

	private void run2() throws InterruptedException, ExecutionException {
		Account cashAccount = Account.builder().id(1).amount(11.1112).type("Cash").key("1").build();
		Account creditAccount = Account.builder().id(2).amount(22.3334).type("Credit").key("2").build();

		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			System.out.println("***********FIRST THREAD CALLED*********");
			redisTransaction.inTransaction(1000, "FIRST CALL");
			redisTransaction.readTransaction();
			System.out.println("***********FIRST THREAD ENDED*********");

			System.out.println("I'll run in a separate thread than the main thread. - 1");
		});

		/*TimeUnit.SECONDS.sleep(1);

		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
			System.out.println("***********SECOND THREAD CALLED*********");
			redisTransaction.inTransaction(1000, "SECOND CALL");
			redisTransaction.readTransaction();
			System.out.println("***********SECOND THREAD ENDED*********");
			System.out.println("I'll run in a separate thread than the main thread. - 2");
		});

		future2.get();*/
		future1.get();

		TimeUnit.SECONDS.sleep(2);
		System.err.println("*******FINAL OUTPUT ***************");
//		redisMultiThreadTransactionRetryTest.readRedis();
		redisTransaction.readTransaction();
	}
}
