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
    private String bigBanRole;
    private Permissions permissions;
    private String pollResultsChannel;

    @Data
    public static class Permissions{
        private List<String> manageLightRoles;
        private List<String> manageHeavyRoles;
        private List<String> managePolls;
    }

}

