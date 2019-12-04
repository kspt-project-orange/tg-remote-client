package kspt.orange.tg_remote_client.tg_client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public final class RequestCodeResult {
    final boolean success;
    @NotNull
    final String phone;
    @NotNull
    final String token;
}
