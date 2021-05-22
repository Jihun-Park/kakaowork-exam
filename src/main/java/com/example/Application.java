package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {
    public static void main(String[] args) {

        String botAppKey = System.getProperty("com.example.botAppKey");
        String userEmail = System.getProperty("com.example.userEmail");

        if(botAppKey == null || botAppKey.length() == 0) {
            System.out.println("** please set property [com.example.botAppKey]");
            return;
        }
        if(userEmail == null || userEmail.length() == 0) {
            System.out.println("** please set property [com.example.userEmail]");
            return;
        }

        SpringApplication.run(Application.class, args);
    }
}