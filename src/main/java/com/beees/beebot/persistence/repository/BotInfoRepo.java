package com.beees.beebot.persistence.repository;

import com.beees.beebot.persistence.domain.BotInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotInfoRepo extends JpaRepository<BotInfoEntity, String> {
    BotInfoEntity findByUsername (String userName);
}
