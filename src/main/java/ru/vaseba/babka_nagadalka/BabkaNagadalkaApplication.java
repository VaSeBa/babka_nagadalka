package ru.vaseba.babka_nagadalka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * Main class for starting only
 */

@SpringBootApplication
public class BabkaNagadalkaApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(BabkaNagadalkaApplication.class, args);
    }

}
