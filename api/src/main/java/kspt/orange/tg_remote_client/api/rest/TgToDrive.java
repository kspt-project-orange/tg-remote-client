package kspt.orange.tg_remote_client.api.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import kspt.orange.tg_remote_client.api.util.RequestValidator;
import kspt.orange.tg_remote_client.drive.DriveService;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_client.TgService;
import kspt.orange.tg_remote_client.tg_to_drive.TgToDriveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/v0/tgToDrive")
public final class TgToDrive implements Api {
    @NotNull
    private final Db db;
    @NotNull
    private final TgToDriveService telegramToDrive;
    @NotNull
    private final RequestValidator requestValidator;

    @PostMapping(path = "/startProcessing", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<StartProcessingResponse> startProcessing(@RequestBody @NotNull final Mono<StartProcessingRequest> body) {
        return body
                .flatMap(requestValidator::validOrEmpty)
                .map(__ -> StartProcessingResponse.OK)
                .defaultIfEmpty(StartProcessingResponse.ERROR)
                .onErrorReturn(StartProcessingResponse.ERROR);
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class StartProcessingRequest implements Request {
        @NotNull
        final String token;
    }

    @RequiredArgsConstructor
    private static final class StartProcessingResponse implements Response {
        @NotNull
        private static final StartProcessingResponse OK = new StartProcessingResponse(Status.OK);
        @NotNull
        private static final StartProcessingResponse ERROR = new StartProcessingResponse(Status.ERROR);

        @NotNull private final Status status;

        private enum Status {
            OK,
            ERROR,
            ;
        }
    }
}
