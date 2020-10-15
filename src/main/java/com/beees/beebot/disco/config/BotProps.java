package com.beees.beebot.disco.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("bot")
public class BotProps {
    private String botUsername;
    private String commandString;
    private Permissions permissions;
    private String colorRole;

    @Data
    public static class Permissions{
        public List<String> canManage;
        private List<String> canBan;
    }

}

