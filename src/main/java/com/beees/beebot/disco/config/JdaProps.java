package com.beees.beebot.disco.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("jda")
public class JdaProps {
    private static String token;

    public static String getCode(){
        return token;
    }
}
