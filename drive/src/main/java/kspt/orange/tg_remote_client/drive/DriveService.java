package kspt.orange.tg_remote_client.drive;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.typesafe.config.Config;
import kspt.orange.tg_remote_client.drive.result.AttachTokenResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
final public class DriveService {
    @NotNull
    private static final String REDIRECT_URI = "";
    @NotNull
    private static final String GOOGLE_APIS_OAUTH_2_TOKEN_URL = "https://oauth2.googleapis.com/token";

    @NotNull
    private final ConcurrentMap<String, DriveClient> clients = new ConcurrentHashMap<>();
    @NotNull
    private final HttpTransport googleHttpTransport = googleHttpTransport();
    @NotNull
    private final JsonFactory googleJsonFactory = JacksonFactory.getDefaultInstance();
    @NotNull
    private final GoogleClientSecrets googleClientSecrets;
    @NotNull
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public DriveService(@NotNull final Config config) {
        this.googleClientSecrets = googleClientSecrets(googleJsonFactory, config);
        this.googleIdTokenVerifier = googleIdTokenVerifier(googleHttpTransport, googleJsonFactory, config);
    }

    @NotNull
    public Mono<AttachTokenResult> attachToken(@NotNull final String token,
                                               @NotNull final String idToken,
                                               @NotNull final String serverAuthCode) {
        return Mono
                .create(sink -> {
                    final var idTokenObject = verifyIdToken(idToken);
                    if (idTokenObject == null) {
                        sink.success(AttachTokenResult.WRONG_ID_TOKEN);
                        return;
                    }

                    final var googleClientSecretsDetails = googleClientSecrets.getDetails();
                    try {
                        final var tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                                googleHttpTransport,
                                googleJsonFactory,
                                GOOGLE_APIS_OAUTH_2_TOKEN_URL,
                                googleClientSecretsDetails.getClientId(),
                                googleClientSecretsDetails.getClientSecret(),
                                serverAuthCode,
                                REDIRECT_URI
                        ).execute();

                        clients.put(token, new DriveClient(
                                googleHttpTransport,
                                googleJsonFactory,
                                googleClientSecrets,
                                tokenResponse.parseIdToken(),
                                tokenResponse.getAccessToken(),
                                tokenResponse.getRefreshToken()
                        ));

                        sink.success(AttachTokenResult.OK);
                    } catch (TokenResponseException e) {
                        sink.success(AttachTokenResult.WRONG_SERVER_AUTH_CODE);
                    } catch (IOException e) {
                        log.error("Error during auth request to google", e);
                        sink.success(AttachTokenResult.ERROR);
                    }
                });
    }

    @SneakyThrows
    @Nullable
    private GoogleIdToken verifyIdToken(@NotNull final String idToken) {
        return googleIdTokenVerifier.verify(idToken);
    }

    @SneakyThrows
    @NotNull
    private static HttpTransport googleHttpTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Error during http transport instantiation", e);
            throw e;
        }
    }

    @SneakyThrows
    @NotNull
    private static GoogleClientSecrets googleClientSecrets(@NotNull final JsonFactory jsonFactory,
                                                           @NotNull final Config config) {
        try {
            return GoogleClientSecrets.load(jsonFactory, new FileReader(DriveService.class.getClassLoader().getResource(config.getString("clientSecretFile")).getFile()));
        } catch (IOException e) {
            log.error("Error during client secrets read", e);
            throw e;
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
