package au.com.flexisoft.redis;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Employee implements Serializable {
    public static final String HASH_KEY = "EMPLOYEE";

    private String id;
    private String name;
    private Integer age;
}