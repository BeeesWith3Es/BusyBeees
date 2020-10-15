package com.beees.beebot.disco.services;

import com.beees.beebot.persistence.domain.BotInfo;
import com.beees.beebot.persistence.repository.BotInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotInfoManager {
    private final BotInfoRepo botInfoRepo;

    public BotInfo getBotInfo(String username){
        return botInfoRepo.findByUsername(username);
    }
}
