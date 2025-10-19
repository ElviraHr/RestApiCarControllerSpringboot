package de.ait.training;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

//аннотация для Ломбока, что мы используем логи:
@Slf4j
public class DevelopementGr54fsApplication {

    public static void main(String[] args) {
        log.info("Starting DevelopementGr54fsApplication");
        SpringApplication.run(DevelopementGr54fsApplication.class, args);
    }

}
