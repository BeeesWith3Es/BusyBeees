package com.beees.beebot;

import com.beees.beebot.disco.config.JdaStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication
public class BeebotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeebotApplication.class, args);
    }

}
