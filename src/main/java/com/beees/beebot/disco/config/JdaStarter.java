package com.beees.beebot.disco.config;

import ch.qos.logback.classic.BasicConfigurator;
import com.beees.beebot.disco.commands.BuzzCommand;
import com.beees.beebot.disco.commands.ProfileCommand;
import com.beees.beebot.disco.services.BotInfoManager;
import com.beees.beebot.persistence.domain.BotInfo;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
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
    private final BuzzCommand buzzCommand;
    private final BotProps botProps;

    public static JDA getJda(){
        return jda;
    }

    @Scheduled(initialDelay = 1, fixedDelay = Long.MAX_VALUE)
    private void start(){
        BotInfo botInfo = botInfoManager.getBotInfo(botProps.getBotUsername());
        //Create Bot
        JDABuilder jdaBuilder = JDABuilder.createDefault(botInfo.getAccessToken());
        //Register commands

        CommandClientBuilder commandBuilder = new CommandClientBuilder();
        commandBuilder.setOwnerId(botInfo.getClientId());
        commandBuilder.setPrefix(botProps.getCommandString());
        commandBuilder.setHelpWord("help");

        //TODO this is a dumb convention: every command needs to store itself.
        //The alternative is dependency injection (these are components), but there's going to be a lot of them.
        commandBuilder.addCommand(ProfileCommand.getCommand());

        CommandClient commandClient = commandBuilder.build();

        jdaBuilder.addEventListeners(commandClient);

        try{
            //Try to log in bot
            jda = jdaBuilder.build();
        }catch (LoginException e){
            e.printStackTrace();
        }

    }
}
