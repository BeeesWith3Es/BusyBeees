package com.beees.beebot.persistence.domain.polling;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "poll_option", schema = "polling")
public class PollOptionE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String emote;

    private String option;

    @OneToMany(mappedBy = "vote")
    private List<PollVoteE> votes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "poll_id")
    private PollE poll;
}
