package com.beees.beebot.persistence.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message", schema = "hive")
public class MessageEntity {

    //TODO add "isBot" and "createTime"

    @Id
    @Column(name = "id")
    private String id;

    private String channelId;

    private String guildId;

    private String memberId;

    private Boolean hasEmbed;

    private ZonedDateTime timestamp;

}
