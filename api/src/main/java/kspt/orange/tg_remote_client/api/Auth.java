package kspt.orange.tg_remote_client.api;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//TODO
@SuppressWarnings("unused")
@RestController
@RequestMapping("/auth")
public final class Auth implements Api {
    @PostMapping(path = "/sendCode", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void sendCode(@RequestBody @NotNull final Map<String, String> json) {
        final var phone = json.get("phone");
        if (phone != null) {
            System.out.println(phone);
        }
    }

    @PostMapping(path = "/signIn", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void signIn(@RequestBody @NotNull final Map<String, String> json) {
        final var phone = json.get("phone");
        final var code = json.get("code");

        if (isNull(phone) || isNull(code)) {
            return;
        }

        System.out.println(phone + " " + code);
    }

    @PostMapping(path = "/pass2FA", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(ACCEPTED)
    public void pass2FA(@RequestBody @NotNull final Map<String, String> json) {
        final var password = json.get("password");
        if (password != null) {
            System.out.println(password);
        }
    }
}
