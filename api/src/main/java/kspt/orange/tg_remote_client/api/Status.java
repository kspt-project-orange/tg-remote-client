package kspt.orange.tg_remote_client.api;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/v0")
public final class Status implements Api {
    private static final Mono<StatusResponse> STATUS_ONLINE_RESPONSE = Mono.just(StatusResponse.ONLINE);

    @GetMapping(path = "/status", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public Mono<? extends Response> status() {
        return STATUS_ONLINE_RESPONSE;
    }

    @RequiredArgsConstructor
    final static class StatusResponse implements Response {
        private static final StatusResponse ONLINE = new StatusResponse("ONLINE");
        @NotNull
        private final String status;
    }
}
