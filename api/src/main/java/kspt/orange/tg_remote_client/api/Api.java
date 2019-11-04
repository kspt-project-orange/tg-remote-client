package kspt.orange.tg_remote_client.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public interface Api {
    static void main(String[] args) {
        SpringApplication.run(Api.class, args);
    }
}
