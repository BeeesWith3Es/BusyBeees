package com.beees.beebot.disco.commands;

import com.beees.beebot.disco.services.MessageService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ChannelBanCommand extends Command {

    private static Command command;
    private final MessageService messageService;

    @PostConstruct
    public void init() {
        command = this;
        this.name = "channel-ban";
        this.aliases = new String[]{"out", "channel-kick"};
        this.help = "";

    }

    @Override
    protected void execute(CommandEvent commandEvent) {

    }

    public static Command getCommand() {
        return command;
    }
}
