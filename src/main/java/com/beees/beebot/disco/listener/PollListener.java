package com.beees.beebot.disco.listener;

import com.beees.beebot.disco.services.PollService;
import com.beees.beebot.persistence.domain.polling.PollE;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PollListener extends ListenerAdapter {

    private final PollService pollService;

    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        PollE poll = pollService.getPoll(event.getMessageId());
        if(poll == null || event.getUser().isBot()){
            return;
        }
        //Emojis will have a null emote, and they will be the name. Emotes will have an emote object, and they are recognized by the id (names change)
        String react = event.getReaction().getReactionEmote().isEmoji() ? event.getReactionEmote().getName() : event.getReactionEmote().getId();
        boolean reactInPoll = poll.getOptions().stream().anyMatch(o -> o.getEmote().equals(react));
        int numVotesInPoll = 0;

        Message message = event.retrieveMessage().complete();
        if(poll.getVoteLimit() != null && poll.getVoteLimit() >= 1){
            numVotesInPoll = message.getReactions()
                    .stream().filter(r -> r.retrieveUsers().complete()
                            .stream().anyMatch(u -> u.getId().equals(event.getUser().getId()))).collect(Collectors.toList()).size();
        }
        if(!reactInPoll || (poll.getVoteLimit() != null && numVotesInPoll > poll.getVoteLimit())){
            try{
                message.removeReaction(event.getReaction().getReactionEmote().getEmote(), event.getUser()).queue();
            }catch(Exception ex){
                message.removeReaction(react, event.getUser()).queue();
            }
            return;
        }

    }
}
