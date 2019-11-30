package kspt.orange.tg_remote_client.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public interface Api {
    @JsonInclude(ALWAYS)
    @JsonAutoDetect(fieldVisibility = ANY)
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
    @JsonAutoDetect(fieldVisibility = ANY)
    interface Response {}
}
