package au.com.flexisoft.redis;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RetryableCache {

    private Integer numberOfRetries = 1;

    //Default is three attempts
    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void retry() {
        System.err.println("***************Entered Retry*************: " + numberOfRetries++);

        if (true) {
            throw new RuntimeException("Retry check");
        }

        System.err.println("***************Exit Retry*************");

    }

    @Recover
    public void recovery(Exception e) {
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.err.println("######### RECOVERY CALLED ##################");
        System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

}
