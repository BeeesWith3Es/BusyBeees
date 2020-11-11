package com.beees.beebot.persistence.domain.polling;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString(exclude = "vote")
@Table(name = "poll_vote", schema = "polling")
public class PollVoteE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "voter_member_id")
    private String voteMemberId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poll_option_id")
    private PollOptionE vote;
}
