package kspt.orange.tg_remote_client.tg_client;

import org.drinkless.tdlib.Client;

public class TgClient {
    static {
        try {
            System.setProperty("java.library.path", "tg-client/libs");
            System.loadLibrary("tdjni");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final var cl = Client.create(__ -> {}, __ -> {}, __ -> {});
        cl.close();
    }
}
