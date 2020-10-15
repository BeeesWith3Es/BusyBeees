package com.beees.beebot.disco.services;

import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    public void reactError(Message m){
        m.addReaction("U+274C").queue();
    }
    public void reactSuccess(Message m){
        m.addReaction("U+2611").queue();
    }
    public void reactBee(Message m){
        m.addReaction("U+1F41D").queue();
    }
}
