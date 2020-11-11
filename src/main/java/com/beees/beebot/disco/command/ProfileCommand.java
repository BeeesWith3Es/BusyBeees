package com.beees.beebot.disco.command;

import com.beees.beebot.disco.config.BotProps;
import com.beees.beebot.disco.services.MessageService;
import com.beees.beebot.persistence.domain.MessageEntity;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileCommand extends Command {

    private static Command command;

    private final BotProps botProps;
    private final MessageService messageService;

    @PostConstruct
    public void init(){
        command = this;
        this.name = "profile";
        this.aliases = new String[]{"stalk", "suss", "hmm"};
        this.help = "Gives information on a mentioned user. Providing a channel after the mention will give info pertinent to that channel.";
    }

    @Override
    protected void execute(CommandEvent ce) {
        //TODO This throws an error when using @mentions
        List<String> args = messageService.parseArgs(ce.getArgs());

        if(args.size()<1){
            //Failure: no one mentioned
            messageService.reactError(ce.getMessage());
            return;
        }
        Member member = ce.getGuild().retrieveMemberById(args.get(0)).complete();
        if(member == null){
            messageService.reactError(ce.getMessage());
            ce.getChannel().sendMessage("Could not find specified user!").queue();
            return;
        }
        if(ce.getMessage().getMentionedChannels().isEmpty()){
            //Non-channel specific profile
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(member.getColor());
            eb.setAuthor(member.getEffectiveName(), null, member.getUser().getAvatarUrl());

            String joinDate = member.getTimeJoined().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
                    .withZone(ZoneId.of("UTC")));
            eb.addField("Join Date: ", joinDate, false);

            Map<String, List<MessageEntity>> messages = messageService.getUserMessages(member.getId());

            int totalMessage = messages.entrySet().stream().reduce(0, (subtotal, element) -> subtotal + element.getValue().size(), Integer::sum);
            Map.Entry<String, List<MessageEntity>> mostUsedChannel =
                    messages.entrySet().stream().max(Comparator.comparingInt(a -> a.getValue().size())).orElse(null);
            String mostUsedChannelString = ce.getGuild().getGuildChannelById(mostUsedChannel.getKey()).getName() + ": " + mostUsedChannel.getValue().size();
            eb.addField("Total Messages: ", String.valueOf(totalMessage), true);
            eb.addField("Most Used Channel: ", mostUsedChannelString, true);
            ce.getChannel().sendMessage(eb.build()).queue();
        }else{
            //Channel specific profile
        }
    }

    public static Command getCommand(){
        return command;
    }
}
