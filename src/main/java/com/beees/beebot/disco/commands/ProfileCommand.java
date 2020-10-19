package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.services.MessageService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileCommand extends Command {


    private static Command command;
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
        //TODO make this work. Rn I'm just testing the JDA-Utilities stuff
        List<String> args = messageService.parseArgs(ce.getArgs());
        if(ce.getMessage().getMentionedMembers().isEmpty()){
            //Failure: no one mentioned
            return;
        }
        if(ce.getMessage().getMentionedChannels().isEmpty()){
            //Non-channel specific profile
        }else{
            //Channel specific profile
        }
    }

    public static Command getCommand(){
        return command;
    }
}
