package com.beees.beebot.persistence.repository;

import com.beees.beebot.persistence.domain.polling.PollE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PollRepo extends JpaRepository<PollE, String> {
    PollE findByMessageId(String messageId);
    List<PollE> findAllByEndTimeBeforeAndClosedFalse(ZonedDateTime now);
}
