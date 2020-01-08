package kspt.orange.tg_remote_client.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
final class DriveClient {
    @NotNull
    private final HttpTransport httpTransport;
    @NotNull
    private final JsonFactory jsonFactory;
    @NotNull
    private final GoogleClientSecrets clientSecrets;
    @NotNull
    private final GoogleIdToken idToken;
    @NotNull
    private final String accessToken;
    @NotNull
    private final String refreshToken;
}
