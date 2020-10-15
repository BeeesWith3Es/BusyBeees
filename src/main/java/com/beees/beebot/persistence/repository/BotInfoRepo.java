package com.beees.beebot.persistence.repository;

import com.beees.beebot.persistence.domain.BotInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotInfoRepo extends JpaRepository<BotInfo, String> {
    BotInfo findByUsername (String userName);
}
