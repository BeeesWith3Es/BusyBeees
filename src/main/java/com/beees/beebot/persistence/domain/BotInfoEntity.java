package com.beees.beebot.persistence.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "bot_info", schema = "hive")
public class BotInfoEntity {

    @Id
    @Column(name = "access_token")
    private String accessToken;

    private String username;

    private String clientId;

    private String guildId;
}
