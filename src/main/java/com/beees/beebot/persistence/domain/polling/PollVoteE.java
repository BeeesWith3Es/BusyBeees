package com.beees.beebot.persistence.domain.polling;

import lombok.Data;
import lombok.Generated;

import javax.persistence.*;

@Data
@Entity
@Table(name = "poll_vote", schema = "polling")
public class PollVoteE {

    @Id
    @Generated
    @Column(name = "id")
    private Integer id;

    @Column(name = "voter_member_id")
    private String voteMemberId;

    @ManyToOne
    @JoinColumn(name = "poll_option_id")
    private PollOptionE vote;
}
