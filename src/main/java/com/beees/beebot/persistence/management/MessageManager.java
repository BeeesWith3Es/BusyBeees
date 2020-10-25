package com.beees.beebot.persistence.management;

import com.beees.beebot.persistence.domain.MessageEntity;
import com.beees.beebot.persistence.repository.MessageRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageManager {

    private final MessageRepo messageRepo;

    public void saveMessage(Message message){
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setChannelId(message.getChannel().getId());
        entity.setGuildId(message.getGuild().getId());
        entity.setMemberId(message.getAuthor().getId());
        entity.setHasEmbed(!message.getEmbeds().isEmpty());
        entity.setTimestamp(message.getTimeCreated().atZoneSameInstant(ZoneOffset.UTC));
        messageRepo.save(entity);
    }

    public void saveMessages(List<Message> messages){
        for(Message m : messages){
            saveMessage(m);
        }
        log.debug("Messages saved");
    }

    public List<MessageEntity> getUserMessages(String memberId){
        return messageRepo.findByMemberId(memberId);
    }

    public List<MessageEntity> getUserMessagesFromChannel(String memberId, String channelId){
        return messageRepo.findByMemberIdAndChannelId(memberId, channelId);
    }

    public MessageEntity getMostRecentMessage(){
        return messageRepo.findTopByOrderByTimestampDesc();
    }

    public MessageEntity getMostRecentMessage(String channelId){
        return messageRepo.findTopByChannelIdOrderByTimestampDesc(channelId);
    }
}
