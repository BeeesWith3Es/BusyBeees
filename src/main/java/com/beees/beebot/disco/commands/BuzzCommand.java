package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.config.JdaStarter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BuzzCommand extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getMessage().getContentRaw().startsWith("o8o ")){
            e.getChannel().sendMessage("Buzz").queue();
        }
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
