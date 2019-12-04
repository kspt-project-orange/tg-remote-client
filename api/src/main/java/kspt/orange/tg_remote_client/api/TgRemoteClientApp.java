package kspt.orange.tg_remote_client.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public interface TgRemoteClientApp {
    static void main(String[] args) {
        SpringApplication.run(TgRemoteClientApp.class, args);
    }
}
