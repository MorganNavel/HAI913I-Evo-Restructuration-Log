package com.example.demo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class Main {
    private static final Logger LOGGER = LogManager.getLogger("MyLogger");

    public static void main(String[] args) {
        LOGGER.log(Level.INFO, "L'application est en cours d'éxécution");
        SpringApplication.run(Main.class, args);
    }
}