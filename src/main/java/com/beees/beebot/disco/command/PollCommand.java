package com.beees.beebot.disco.command;

import com.beees.beebot.disco.config.BotProps;
import com.beees.beebot.disco.config.JdaStarter;
import com.beees.beebot.disco.services.MessageService;
import com.beees.beebot.disco.services.PollService;
import com.beees.beebot.disco.services.TimeService;
import com.beees.beebot.persistence.domain.polling.PollE;
import com.beees.beebot.persistence.domain.polling.PollOptionE;
import com.beees.beebot.persistence.domain.polling.PollVoteE;
import com.beees.beebot.persistence.management.PollManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollCommand extends Command {

    private static Command command;
    private final MessageService messageService;
    private final PollService pollService;
    private final TimeService timeService;
    private final BotProps botProps;

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
        Integer voteLimit = null;
        MessageChannel channel = e.getMessage().getChannel();
        if(!e.getMessage().getMentionedChannels().isEmpty()){
            channel = e.getMessage().getMentionedChannels().get(0);
            args.remove(args.size()-1);
        }
        if(!args.get(0).contains("\"")){
            try{
                voteLimit = Integer.parseInt(args.remove(0));
            }catch(Exception ex){
                messageService.reactError(e.getMessage());
                return;
            }

        }
        ZonedDateTime pollEndTime = null;
        try{
            pollEndTime = timeService.getTimeFromNow(timeLimit);
        }catch(Exception ex){
            messageService.reactError(e.getMessage());
            return;
        }
        Map<String, String> pollOptions = new HashMap<>();
        if(voteLimit != null && voteLimit > 0){
            message = message.concat("\r\n# Votes Allowed: "+voteLimit);
        }
        for(int i = 0; i < args.size(); i+=2){
            String option = args.get(i).substring(1, args.get(i).length()-1);
            String emote = args.get(i+1);
            pollOptions.put(option, emote);
            message = message.concat("\r\n"+emote+": "+option);
        }
        PollE poll = new PollE();
        poll.setPollQuestion(message.trim());
        message = message.concat("\r\nPoll ends "+timeService.getPrettyTime(pollEndTime));
        Message pollMessage = channel.sendMessage(message).complete();
        List<PollOptionE> pollOptionEntities = new ArrayList<>();

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

        poll.setEndTime(pollEndTime);
        poll.setMessageId(pollMessage.getId());
        poll.setPollsterMemberId(e.getAuthor().getId());
        poll.setOptions(pollOptionEntities);
        poll.setVoteLimit(voteLimit);
        poll.setGuildId(pollMessage.getGuild().getId());
        poll.setChannelId(pollMessage.getChannel().getId());
        pollService.savePoll(poll);
    }

    private PollOptionE createPollOption(PollE poll, String option, String emote){
        PollOptionE optionEntity = new PollOptionE();
        optionEntity.setOption(option);
        optionEntity.setEmote(emote);
        optionEntity.setPoll(poll);
        return optionEntity;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public void completePolls(){
        JDA jda = JdaStarter.getJda();
        List<PollE> unclosedPolls = pollService.getCompletedUnclosedPolls();
        for(PollE poll : unclosedPolls){
            Message message = Optional.ofNullable(jda.getGuildById(poll.getGuildId()))
                    .map(g-> g.getTextChannelById(poll.getChannelId()))
                    .map(tc-> tc.retrieveMessageById(poll.getMessageId()).complete())
                    .orElse(null);
            if(message != null){
                final StringBuilder resultsMessage = new StringBuilder();
                List<MessageReaction> reactions = message.getReactions();
                final int totalVotes = reactions.stream().mapToInt(MessageReaction::getCount).sum()-reactions.size();
                reactions.forEach(
                        r ->{
                            PollOptionE pollOption = Optional.of(poll.getOptions().stream()
                                    .filter(o -> o.getEmote().equals(r.getReactionEmote().isEmoji() ? r.getReactionEmote().getName() : r.getReactionEmote().getId()))
                                    .collect(Collectors.toList())).map(l -> l.get(0)).orElse(null);
                            List<PollVoteE> votes = new ArrayList<>();
                            if(pollOption != null){
                                resultsMessage.append("```\r\n").append(pollOption.getOption()).append(": ").append(r.getCount()-1).append("\r\n");
                                if(r.getCount()<=1){
                                    resultsMessage.append(":(");
                                }
                                for(int i = 0; i<(((double)r.getCount()-1)/(double)totalVotes)*20; i++){
                                    resultsMessage.append("|");
                                }
                                resultsMessage.append("```\r\n");
                                r.retrieveUsers().complete().forEach(
                                        u ->{
                                            if(!u.isBot()) {
                                                PollVoteE vote = new PollVoteE();
                                                vote.setVoteMemberId(u.getId());
                                                vote.setVote(pollOption);
                                                votes.add(vote);
                                            }
                                        }
                                );
                                pollOption.setVotes(votes);
                            }
                        }
                );
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(0xFFD411);
                eb.setAuthor(botProps.getBotUsername(), jda.getSelfUser().getAvatarUrl());
                eb.setTitle(poll.getPollQuestion());
                eb.addField("RESULTS:", resultsMessage.toString(), false);
                Optional.ofNullable(jda.getGuildById(poll.getGuildId())).map(g->g.getTextChannelById(botProps.getPollResultsChannel())).ifPresent(
                        textChannel -> textChannel.sendMessage(eb.build()).queue()
                );
                poll.setClosed(true);
                pollService.savePoll(poll);
                log.info("Poll completed: "+poll.getMessageId());
            }
        }
    }

    public static Command getCommand() {
        return command;
    }
}
