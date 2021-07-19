package au.com.flexisoft.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class)
public class RedisCacheHashOperationTest {

    @Autowired
    private RedisCacheHashOperation<Employee> redisCacheHashOperation;

    @Test
    public void testRedisCacheHashOperation() {

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

        Employee value = redisCacheHashOperation.getValue(Employee.HASH_KEY, "2");

        assertEquals("p2",value.getName());

        assertTrue(true);
    }
}