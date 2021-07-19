package au.com.flexisoft.redis;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class Account implements Serializable {
    public static final String HASH_KEY = "ACCOUNT";

    private Integer id;
    private String type;
    private Double amount;
    private String key;
}
