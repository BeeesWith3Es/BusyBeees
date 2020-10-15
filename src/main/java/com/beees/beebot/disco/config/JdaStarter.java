package com.beees.beebot.disco.config;

import ch.qos.logback.classic.BasicConfigurator;
import com.beees.beebot.disco.commands.BuzzCommand;
import com.beees.beebot.disco.services.BotInfoManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;


@Component
@RequiredArgsConstructor
public class JdaStarter {
    private static JDA jda;

    private final BotInfoManager botInfoManager;
    private final JdaProps jdaProps;

    public static JDA getJda(){
        return jda;
    }

    @Scheduled(initialDelay = 1, fixedDelay = Long.MAX_VALUE)
    private void start(){
        //Create Bot
        JDABuilder jdaBuilder = JDABuilder.createDefault(botInfoManager.getBotInfo(jdaProps.getBotUsername()).getAccessToken());
        //Register commands
        jdaBuilder.addEventListeners(new BuzzCommand());
        try{
            //Try to log in bot
            jda = jdaBuilder.build();
        }catch (LoginException e){
            e.printStackTrace();
        }
    }
}
