package kspt.orange.tg_remote_client.drive;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

final class DriveClient {
    @NotNull
    private static final String APPLICATION_NAME = "TgRemoteClient";
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
    @NotNull
    private final GoogleCredential credential;
    @NotNull
    private final Drive drive;

    DriveClient(@NotNull final HttpTransport httpTransport,
                @NotNull final JsonFactory jsonFactory,
                @NotNull final GoogleClientSecrets clientSecrets,
                @NotNull final GoogleIdToken idToken,
                @NotNull final String accessToken,
                @NotNull final String refreshToken) {
        this.httpTransport = httpTransport;
        this.jsonFactory = jsonFactory;
        this.clientSecrets = clientSecrets;
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        this.credential = new GoogleCredential().setAccessToken(accessToken);
        this.drive = new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

//        final var file = new File();
//        file.setName("myFile.txt");
//        try {
//            final var filePath = java.io.File.createTempFile("myFile", ".txt");
//            filePath.deleteOnExit();
//            BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
//            out.write("Hello from Anton");
//            out.close();
//            final var mediaContent = new FileContent("text/plain", filePath);
//            final var res = drive.files().create(file, mediaContent).setFields("id").execute();
//            System.out.println(res.getId());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
