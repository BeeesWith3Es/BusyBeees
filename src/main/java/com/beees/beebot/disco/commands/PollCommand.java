package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.services.MessageService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PollCommand extends Command {

    private static Command command;
    private final MessageService messageService;

    @PostConstruct
    public void init() {
        command = this;
        this.name = "poll";
        this.aliases = new String[]{};
        this.help = "";
    }

    @Override
    protected void execute(CommandEvent e) {
        List<String> args = messageService.parseArgs(e.getArgs());
        args = args.stream().filter(s-> Optional.ofNullable(s).map(ss->!ss.isEmpty()).orElse(false)).collect(Collectors.toList());
        if(args.size()<4 || args.size() > 15){
            messageService.reactError(e.getMessage());
            return;
        }
        String message = args.remove(0);
        String timeLimit = args.remove(0);
        String voteLimit = "-1";
        if(!args.get(0).contains("\"")){
            voteLimit = args.remove(0);
        }
        Map<String, String> pollOptions = new HashMap<>();
        for(int i = 0; i < args.size(); i+=2){
            pollOptions.put(args.get(i), args.get(i+1));
        }
        Message pollMessage = e.getChannel().sendMessage(message).complete();
        for (Map.Entry<String, String> option : pollOptions.entrySet()){
            pollMessage.addReaction(option.getValue()).queue((success)->{ }, (failure)->{
                Emote emote = e.getJDA().getEmoteById(option.getValue().replaceAll("[^0-9]", ""));
                if(emote == null){
                    pollMessage.editMessage("Poll Creation failed. Ensure that all emotes used are from this server.").queue();
                    pollMessage.clearReactions().queue();
                    return;
                }
                pollMessage.addReaction(emote).queue();
            });
        }
    }

    public static Command getCommand() {
        return command;
    }
}
