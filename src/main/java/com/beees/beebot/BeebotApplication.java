package com.beees.beebot;

import com.beees.beebot.disco.config.JdaStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication
public class BeebotApplication {
    //TODO some emoji regex I found for later: "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])"
    public static void main(String[] args) {
        SpringApplication.run(BeebotApplication.class, args);
    }

}
