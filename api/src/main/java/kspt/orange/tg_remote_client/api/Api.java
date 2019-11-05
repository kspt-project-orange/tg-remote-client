package kspt.orange.tg_remote_client.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@SpringBootApplication
public interface Api {
    static void main(String[] args) {
        SpringApplication.run(Api.class, args);
    }

    interface Request {
        default boolean isValid() {
            for (final var field : getClass().getDeclaredFields()) {
                try {
                    if (!field.canAccess(this)) {
                        field.setAccessible(true);
                    }
                    if (field.get(this) == null) {
                        System.out.println(field.getName());
                        return false;
                    }
                } catch (Exception e) {
                    System.out.println(field.getName());
                    return false;
                }
            }
            return true;
        }
    }

    @JsonInclude(NON_NULL)
    interface Response{}
}
