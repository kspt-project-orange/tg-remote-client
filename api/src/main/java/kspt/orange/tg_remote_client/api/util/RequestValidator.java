package kspt.orange.tg_remote_client.api.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RequiredArgsConstructor
public final class RequestValidator {
    public <T> boolean isValid(@NotNull final T o) {
        for (final var field : o.getClass().getDeclaredFields()) {
            if (field.getAnnotation(OptionalField.class) != null) {
                continue;
            }
            try {
                if (!field.canAccess(o)) {
                    field.setAccessible(true);
                }
                if (field.get(o) == null) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OptionalField {}
}
