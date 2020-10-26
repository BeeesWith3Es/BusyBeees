package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.services.MessageService;
import com.beees.beebot.persistence.domain.polling.PollE;
import com.beees.beebot.persistence.domain.polling.PollOptionE;
import com.beees.beebot.persistence.management.PollManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.tomcat.jni.Poll;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PollCommand extends Command {

    private static Command command;
    private final MessageService messageService;
    private final PollManager pollManager;

    @PostConstruct
    public void init() {
        command = this;
        this.name = "poll";
        this.aliases = new String[]{};
        this.help = "";
    }

    @Override
    @Transactional
    protected void execute(CommandEvent e) {
        List<String> args = messageService.parseArgs(e.getArgs());
        args = args.stream().filter(s-> Optional.ofNullable(s).map(ss->!ss.isEmpty()).orElse(false)).collect(Collectors.toList());
        if(args.size()<4 || args.size() > 16){
            messageService.reactError(e.getMessage());
            return;
        }
        String message = args.remove(0);
        message = message.substring(1, message.length()-1);
        String timeLimit = args.remove(0);
        String voteLimit = "-1";
        MessageChannel channel = e.getMessage().getChannel();
        if(!e.getMessage().getMentionedChannels().isEmpty()){
            channel = e.getMessage().getMentionedChannels().get(0);
            args.remove(args.size()-1);
        }
        if(!args.get(0).contains("\"")){
            voteLimit = args.remove(0);
        }
        Map<String, String> pollOptions = new HashMap<>();
        for(int i = 0; i < args.size(); i+=2){
            String option = args.get(i).substring(1, args.get(i).length()-1);
            String emote = args.get(i+1);
            pollOptions.put(option, emote);
            message = message.concat("\r"+emote+": "+option);
        }
        Message pollMessage = channel.sendMessage(message).complete();
        List<PollOptionE> pollOptionEntities = new ArrayList<>();
        PollE poll = new PollE();
        for (Map.Entry<String, String> option : pollOptions.entrySet()){
            try{
                pollMessage.addReaction(option.getValue()).complete();
                pollOptionEntities.add(createPollOption(poll, option.getKey(), option.getValue()));
            }catch(ErrorResponseException error){
                String emoteCode = option.getValue().replaceAll("[^0-9]", "");
                        Emote emote = e.getJDA().getEmoteById(emoteCode);
                        if(emote == null){
                            pollMessage.editMessage("Poll Creation failed. Ensure that all emotes used are from this server.").queue();
                            pollMessage.clearReactions().queue();
                            return;
                        }
                        pollOptionEntities.add(createPollOption(poll, option.getKey(), emoteCode));
                        pollMessage.addReaction(emote).complete();
            }
        }

        poll.setEndTime(ZonedDateTime.now().plusMinutes(5));
        poll.setMessageId(pollMessage.getId());
        poll.setPollsterMemberId(e.getAuthor().getId());
        poll.setOptions(pollOptionEntities);
        pollManager.savePoll(poll);
    }

    private PollOptionE createPollOption(PollE poll, String option, String emote){
        PollOptionE optionEntity = new PollOptionE();
        optionEntity.setOption(option);
        optionEntity.setEmote(emote);
        optionEntity.setPoll(poll);
        return optionEntity;
    }

    public static Command getCommand() {
        return command;
    }
}
