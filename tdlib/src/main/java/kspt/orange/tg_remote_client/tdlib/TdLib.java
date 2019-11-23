package kspt.orange.tg_remote_client.tdlib;

import org.drinkless.tdlib.Client;

public class TdLib {
    static {
        try {
            System.setProperty("java.library.path", "tdlib/libs");
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
