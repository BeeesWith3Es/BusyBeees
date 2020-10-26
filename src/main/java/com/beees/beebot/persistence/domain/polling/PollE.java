package com.beees.beebot.persistence.domain.polling;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "poll", schema = "polling")
public class PollE {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "pollster_member_id")
    private String pollsterMemberId;

    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "vote_limit")
    private Integer voteLimit;

    private Boolean closed = false;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @OneToMany(mappedBy = "poll", cascade = { CascadeType.ALL,CascadeType.PERSIST,CascadeType.MERGE })
    private List<PollOptionE> options;
}
