package au.com.flexisoft.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@SpringBootApplication
public class RedisTransactionThread implements CommandLineRunner {

	@Autowired
	private RedisMultiThreadTransactionRetryTest redisMultiThreadTransactionRetryTest;


	public static void main(String[] args) {
		System.out.println("Redis Demo");
		SpringApplication.run(RedisTransactionThread.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println("Inside run");

		/*redisMultiThreadTransactionRetryTest.populateRedis();
		redisMultiThreadTransactionRetryTest.readRedis();*/

		Account cashAccount = Account.builder().id(1).amount(11.1112).type("Cash").key("1").build();
		Account creditAccount = Account.builder().id(2).amount(22.3334).type("Credit").key("2").build();

		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			try {
				System.out.println("***********FIRST THREAD CALLED*********");
				redisMultiThreadTransactionRetryTest.populateRedis(cashAccount, creditAccount);
				redisMultiThreadTransactionRetryTest.readRedis();
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
			redisMultiThreadTransactionRetryTest.readRedis();
			System.out.println("***********SECOND THREAD ENDED*********");
			System.out.println("I'll run in a separate thread than the main thread. - 2");
		});

		future2.get();
		future1.get();

		System.err.println("*******FINAL OUTPUT ***************");
		redisMultiThreadTransactionRetryTest.readRedis();

//		callAsync();

	}


	@Async
	public void callAsync() throws InterruptedException {
		System.out.println("callAsynch called: ");
		printCalled(10000);
		printCalled(5000);
		printCalled(1000);
	}

	public void printCalled(Integer time) throws InterruptedException {
		Thread.sleep(time);
		System.out.println("printCalled: "+time);
	}
}