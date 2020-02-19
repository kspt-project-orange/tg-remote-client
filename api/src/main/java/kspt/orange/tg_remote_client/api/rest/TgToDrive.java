package kspt.orange.tg_remote_client.api.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import kspt.orange.tg_remote_client.api.util.RequestValidator;
import kspt.orange.tg_remote_client.postgres_db.Db;
import kspt.orange.tg_remote_client.tg_to_drive.TgToDriveService;
import kspt.orange.tg_remote_client.tg_to_drive.result.StartProcessingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
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

    @GetMapping(path = "/isProcessing", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @NotNull
    public Mono<IsProcessingResponse> isProcessing(@RequestBody @NotNull final Mono<IsProcessingRequest> body) {
        return body
                .filter(requestValidator::isValid)
                .map(requestBody -> requestBody.token)
                .filterWhen(db::isValidToken)
                .flatMap(telegramToDrive::isProcessing)
                .map(IsProcessingResponse::new);
    }

    @PostMapping(path = "/startProcessing", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    @NotNull
    public Mono<StartProcessingResponse> startProcessing(@RequestBody @NotNull final Mono<StartProcessingRequest> body) {
        return body
                .filter(requestValidator::isValid)
                .map(requestBody -> requestBody.token)
                .filterWhen(db::isValidToken)
                .flatMap(telegramToDrive::startProcessing)
                .map(StartProcessingResponse::new)
                .defaultIfEmpty(StartProcessingResponse.ERROR)
                .onErrorReturn(StartProcessingResponse.ERROR);
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class IsProcessingRequest implements Request {
        @NotNull
        final String token;
    }

    @RequiredArgsConstructor
    private static final class IsProcessingResponse implements Response {
        @NotNull
        final Boolean isProcessing;
    }

    @RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
    private static final class StartProcessingRequest implements Request {
        @NotNull
        final String token;
    }

    @RequiredArgsConstructor
    private static final class StartProcessingResponse implements Response {
        @NotNull
        static final StartProcessingResponse OK = new StartProcessingResponse(StartProcessingResult.OK);
        @NotNull
        static final StartProcessingResponse ERROR = new StartProcessingResponse(StartProcessingResult.ERROR);

        @NotNull
        final StartProcessingResult status;

        @NotNull
        static StartProcessingResponse of(@NotNull final StartProcessingResult status) {
            switch (status) {
                case OK:
                    return OK;
                case ERROR:
                default:
                    return ERROR;
            }
        }
    }
}
