package com.beees.beebot.persistence.repository;

import com.beees.beebot.persistence.domain.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<MessageEntity, String> {
    MessageEntity findTopByOrderByTimestampDesc();
    MessageEntity findTopByChannelIdOrderByTimestampDesc(String channelId);
    List<MessageEntity> findByMemberId(String memberId);
    List<MessageEntity> findByMemberIdAndChannelId(String memberId, String channelId);
}
