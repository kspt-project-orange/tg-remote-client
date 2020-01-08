package kspt.orange.tg_remote_client.drive;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.typesafe.config.Config;
import kspt.orange.rg_remote_client.commons.exceptions.Exceptions;
import kspt.orange.tg_remote_client.drive.result.AttachTokenResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
final public class DriveService {
    @NotNull
    private final ConcurrentHashMap<String, DriveClient> clients = new ConcurrentHashMap<>();
    @NotNull
    private final HttpTransport googleHttpTransport = googleHttpTransport();
    @NotNull
    private final JsonFactory googleJsonFactory = JacksonFactory.getDefaultInstance();
    @NotNull
    private final Config config;
    @NotNull
    private final GoogleClientSecrets googleClientSecrets;
    @NotNull
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public DriveService(@NotNull final Config config) {
        this.config = config;
        this.googleClientSecrets = googleClientSecrets(googleJsonFactory, config);
        this.googleIdTokenVerifier = googleIdTokenVerifier(googleHttpTransport, googleJsonFactory, config);
    }

    @NotNull
    public Mono<AttachTokenResult> attachToken(@NotNull final String token,
                                               @NotNull final String idToken,
                                               @NotNull final String serverAuthCode) {
        return Mono
                .fromCallable(() -> {
                    final var idTokenObject = googleIdTokenVerifier.verify(idToken);
                    if (idTokenObject == null) {
                        return AttachTokenResult.WRONG_ID_TOKEN;
                    }

                    try {
                        final var tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                                googleHttpTransport,
                                googleJsonFactory,
                                "https://oauth2.googleapis.com/token",
                                googleClientSecrets.getDetails().getClientId(),
                                googleClientSecrets.getDetails().getClientSecret(),
                                serverAuthCode,
                                "").execute();

                        clients.put(token, new DriveClient(
                                googleHttpTransport,
                                googleJsonFactory,
                                googleClientSecrets,
                                tokenResponse.parseIdToken(),
                                tokenResponse.getAccessToken(),
                                tokenResponse.getRefreshToken()
                        ));

                        return AttachTokenResult.OK;
                    } catch (TokenResponseException e) {
                        return AttachTokenResult.WRONG_SERVER_AUTH_CODE;
                    }
                });
    }

    @NotNull
    private static HttpTransport googleHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.info("Error during http transport instantiation", e);
            throw Exceptions.uncheckedFromChecked(e);
        }
    }

    @NotNull
    private static GoogleClientSecrets googleClientSecrets(@NotNull final JsonFactory jsonFactory,
                                                           @NotNull final Config config) {
        try {
            return GoogleClientSecrets.load(jsonFactory, new FileReader(DriveService.class.getClassLoader().getResource(config.getString("clientSecretFile")).getFile()));
        } catch (IOException e) {
            log.info("Error during client secrets read", e);
            throw Exceptions.uncheckedFromChecked(e);
        }
    }

    @NotNull
    private static GoogleIdTokenVerifier googleIdTokenVerifier(@NotNull final HttpTransport httpTransport,
                                                               @NotNull final JsonFactory jsonFactory,
                                                               @NotNull final Config config) {
        return new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(config.getStringList("clientIds"))
                .build();
    }
}
