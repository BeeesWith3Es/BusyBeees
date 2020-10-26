package com.beees.beebot.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value = {AuditingEntityListener.class})
@Table(name = "message", schema = "hive")
public class MessageEntity {

    //TODO add "isBot" and "createTime"

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "guild_id")
    private String guildId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "has_embed")
    private Boolean hasEmbed;

    private ZonedDateTime timestamp;

    @Column(name = "is_bot")
    private Boolean isBot;

    @CreatedDate
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
}
