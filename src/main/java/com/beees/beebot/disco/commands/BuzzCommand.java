package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.config.BotProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuzzCommand extends ListenerAdapter {

    private final BotProps botProps;

    public void onMessageReceived(MessageReceivedEvent e){
        String message = e.getMessage().getContentRaw();
        if(botProps.getCommandString().trim().equals(message)){
            e.getMessage().addReaction("U+1F41D").queue();
        }
    }

    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        if(event.getUser().isBot()){
            return;
        }
        event.retrieveMessage().queue((message -> {
            if(event.getJDA().getSelfUser().getId().equals(message.getAuthor().getId())){
                if(event.getReactionEmote().getName().equals("🐝")){
                    message.addReaction("U+1F36F").queue(reaction->{
                        message.removeReaction("U+1F36F").queueAfter(2, TimeUnit.SECONDS);
                        message.removeReaction("🐝", event.getMember().getUser()).queue();
                    });
                }
            }
        }));
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    void giveTime(){
        //TODO find a way to handle first time setup of messages with functionality
//        JDA jda = JdaStarter.getJda();
//        for(Guild g : jda.getGuilds()){
//            g.getTextChannelsByName("bot-check", true).get(0)
//                    .sendMessage("Click the Honey Bee to check if I'm around!").queue(message->{
//                message.addReaction("🐝").queue();
//            });
//        }
    }
}
