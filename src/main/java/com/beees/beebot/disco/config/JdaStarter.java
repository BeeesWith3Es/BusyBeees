package com.beees.beebot.disco.config;

import com.beees.beebot.disco.listener.BuzzListener;
import com.beees.beebot.disco.command.ColorCommand;
import com.beees.beebot.disco.command.PollCommand;
import com.beees.beebot.disco.command.ProfileCommand;
import com.beees.beebot.disco.listener.PollListener;
import com.beees.beebot.disco.services.MessageService;
import com.beees.beebot.persistence.management.BotInfoManager;
import com.beees.beebot.persistence.domain.BotInfoEntity;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;


@Component
@RequiredArgsConstructor
public class JdaStarter {
    private static JDA jda;

    private final BotInfoManager botInfoManager;
    private final BuzzListener buzzListener;
    private final PollListener pollListener;
    private final BotProps botProps;
    private final MessageService messageService;

    public static JDA getJda(){
        return jda;
    }

    @Scheduled(initialDelay = 1, fixedDelay = Long.MAX_VALUE)
    private void start(){
        BotInfoEntity botInfo = botInfoManager.getBotInfo(botProps.getBotUsername());
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
        commandBuilder.addCommand(ColorCommand.getCommand());
        commandBuilder.addCommand(PollCommand.getCommand());

        CommandClient commandClient = commandBuilder.build();

        jdaBuilder.addEventListeners(commandClient);
        jdaBuilder.addEventListeners(buzzListener);
        jdaBuilder.addEventListeners(pollListener);

        try{
            //Try to log in bot
            jda = jdaBuilder.build();
        }catch (LoginException e){
            e.printStackTrace();
            return;
        }
        messageService.scrapeMessages();
    }
}
