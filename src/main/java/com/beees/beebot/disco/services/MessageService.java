package com.beees.beebot.disco.services;

import com.beees.beebot.disco.config.BotProps;
import com.beees.beebot.disco.config.JdaStarter;
import com.beees.beebot.persistence.domain.BotInfoEntity;
import com.beees.beebot.persistence.domain.MessageEntity;
import com.beees.beebot.persistence.management.BotInfoManager;
import com.beees.beebot.persistence.management.MessageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageManager messageManager;
    private final BotInfoManager botInfoManager;
    private final BotProps botProps;

    public void reactError(Message m) {
        m.addReaction("U+274C").queue();
    }

    public void reactSuccess(Message m) {
        m.addReaction("U+2611").queue();
    }

    public void reactBee(Message m) {
        m.addReaction("U+1F41D").queue();
    }

    public void reactHoneyPot(Message m) {
        m.addReaction("U+1F36F").queue();
    }

    public List<String> parseArgs(String args) {
        return new ArrayList<>(Arrays.asList(args.split("\\s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")));
    }

    public Map<String, List<MessageEntity>> getUserMessages(String memberId){
        List<MessageEntity> messages = messageManager.getUserMessages(memberId);
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream()
                .collect(Collectors.groupingBy(MessageEntity::getChannelId));
    }

    public int getNumberOfUserMessages(String memberId){
        List<MessageEntity> messages = messageManager.getUserMessages(memberId);
        return messages == null ? 0 : messages.size();
    }

    public int getNumberOfUserMessages(String memberId, String channelId){
        List<MessageEntity> messages = messageManager.getUserMessagesFromChannel(memberId, channelId);
        return messages == null ? 0 : messages.size();
    }

    public void scrapeMessages() {
        long timeBefore = System.currentTimeMillis();
        BotInfoEntity botInfo = botInfoManager.getBotInfo(botProps.getBotUsername());
        Guild guild;
        try{
            guild = JdaStarter.getJda().awaitReady().getGuildById(botInfo.getGuildId());
        }catch (Exception e){
            log.error("{}", e.getMessage());
            return;
        }
        log.info("Scrape started.");
        MessageHistory history;

        Map<String, List<Message>> messagesByChannel = new HashMap<>();
        List<TextChannel> channels = guild == null ? new ArrayList<>() : guild.getTextChannels();
        for(TextChannel tc : channels){

            MessageEntity mostRecentMessage = messageManager.getMostRecentMessage(tc.getId());
            final int BATCH_SIZE = 100;
            if(mostRecentMessage != null){
                history = MessageHistory.getHistoryAfter(tc, mostRecentMessage.getId()).limit(BATCH_SIZE).complete();
            }else{
                history = MessageHistory.getHistoryFromBeginning(tc).limit(BATCH_SIZE).complete();
            }

            List<Message> messages = new ArrayList<>(history.getRetrievedHistory());
            if(messages.isEmpty()){
                messagesByChannel.put(tc.getId(), new ArrayList<>());
                continue;
            }
            Message lastMessage = messages.get(0);
            int messagesReceived = history.size();
            int increment = 0;
            while (messagesReceived >= BATCH_SIZE) {
                MessageHistory batch = MessageHistory.getHistoryAfter(tc, lastMessage.getId()).limit(BATCH_SIZE).complete();
                messages.addAll(batch.getRetrievedHistory());
                messagesReceived = batch.getRetrievedHistory().size();
                lastMessage = messages.get(increment* BATCH_SIZE);
                increment++;
            }
            messagesByChannel.put(tc.getId(), messages);
        }

        for(Map.Entry<String, List<Message>> e : messagesByChannel.entrySet()){
            if(!e.getValue().isEmpty()){
                messageManager.saveMessages(e.getValue());
            }
        }
        log.info("Time to scrape: {}s", (double)(System.currentTimeMillis()-timeBefore)/1000);
    }
}
