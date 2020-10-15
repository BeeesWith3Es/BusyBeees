package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.config.BotProps;
import com.beees.beebot.disco.config.JdaStarter;
import com.beees.beebot.disco.services.ColorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuzzCommand extends ListenerAdapter {

    private final BotProps botProps;
    private final ColorService colorService;

    public void onMessageReceived(MessageReceivedEvent e){
        long debugTime = System.currentTimeMillis();
        String message = e.getMessage().getContentRaw();
        if(botProps.getCommandString().equals(message)){
            e.getMessage().addReaction("U+1F41D").queue();
        }else if(message.startsWith(botProps.getCommandString())){
            List<String> args = new ArrayList<>(Arrays.asList(message.split(" ")));
            args.remove(0);
            switch(args.get(0)){
                case "color":
                    args.remove(0);
                    colorService.colorCommand(args, e);
                    break;
                default:
                    e.getChannel().sendMessage("I don't know that command!").queue();
            }
        }
        log.debug("Message Received took "+(System.currentTimeMillis()-debugTime)+" millis");
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    void giveTime(){
//        JDA jda = JdaStarter.getJda();
//        for(Guild g : jda.getGuilds()){
//            g.getTextChannelsByName("bot-check", true).get(0)
//                    .sendMessage("Cron fired. Time is: "+new Date()).queue();
//        }
    }
}
