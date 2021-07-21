package au.com.flexisoft.redis;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisTransactionValueOperationTest implements CommandLineRunner {

	@Autowired
	private RedisTransactionValueOperation redis;

	public static void main(String[] args) {
		System.out.println("Redis Demo");
		SpringApplication.run(RedisTransactionValueOperationTest.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		System.out.println("Inside run");

		run1();
	}



	private void run1() throws InterruptedException, ExecutionException {
		Account cashAccount = Account.builder().id(1).amount(11.1112).type("Cash").key("1").build();
		Account creditAccount = Account.builder().id(2).amount(22.3334).type("Credit").key("2").build();

		CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
			System.out.println("***********FIRST THREAD CALLED*********");
			redis.transaction1(10000, "FIRST CALL");
			redis.readTransaction();
			System.out.println("***********FIRST THREAD ENDED*********");
		});

		TimeUnit.SECONDS.sleep(2);

		CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
			System.out.println("***********SECOND THREAD CALLED*********");
			redis.transaction1(1000, "SECOND CALL");
			redis.readTransaction();
			System.out.println("***********SECOND THREAD ENDED*********");
			System.out.println("I'll run in a separate thread than the main thread. - 2");
		});

		future2.get();
		future1.get();

		TimeUnit.SECONDS.sleep(2);
		System.err.println("*******FINAL OUTPUT ***************");
//		redisMultiThreadTransactionRetryTest.readRedis();
//		redisTransaction.readTransaction();
	}
}
