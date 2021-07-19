package au.com.flexisoft.redis;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisDemoApplication implements CommandLineRunner {

	@Autowired
	private RedisSpringTransaction redisSpringTransaction;

	@Autowired
	private RedisCacheHashOperation redisCacheHashOperation;

	@Autowired
	private RedisTransaction redisTransaction;

	@Autowired
	private RetryableCache retryableCache;

	public static void main(String[] args) {
		System.out.println("Redis Demo");
		SpringApplication.run(RedisDemoApplication.class, args);
	}

	private  void processRedisClientCall() {
		Employee emp1 = new Employee("1", "p1", 11);
		Employee emp2 = new Employee("2", "p2", 12);
		Employee emp3 = new Employee("3", "p3", 13);
		Employee emp4 = new Employee("4", "p4", 14);
		Employee emp5 = new Employee("5", "p5", 15);

		redisCacheHashOperation.putValue(Employee.HASH_KEY, emp1.getId(), emp1);
		redisCacheHashOperation.putValue(Employee.HASH_KEY, emp2.getId(), emp2);
		redisCacheHashOperation.putValue(Employee.HASH_KEY, emp3.getId(), emp3);
		redisCacheHashOperation.putValue(Employee.HASH_KEY, emp4.getId(), emp4);
		redisCacheHashOperation.putValue(Employee.HASH_KEY, emp5.getId(), emp5);

		Map employees = redisCacheHashOperation.findAll("EMPLOYEE");

		System.out.println(employees.size());
		System.out.println("---------------------");
		employees.entrySet().forEach(System.out::println);
		System.out.println("---------------------");
		System.out.println(redisCacheHashOperation.getValue("EMPLOYEE", "2"));
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Inside run");

//		processRedisClientCall();

	/*	try {
			redisSpringTransaction.performTransactional();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("INSIDE CALLING APPs EXCEPTION");
		}*/


//		redisSpringTransaction.readFromCache();

		/*******************************************/

//		System.out.println("******************************");
//		redisTransaction.readTransaction();
//		System.out.println("************Before Transaction Call***************");
//		try {
//			redisTransaction.inTransaction();
//		} catch (Exception e) {
//			System.err.println("****EXCEPTION*****");
//			e.printStackTrace();
//
//		}
//		System.out.println("***********After Transaction Call*************");
//		redisTransaction.readTransaction();


		retryableCache.retry();

	}
}
