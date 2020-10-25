package com.beees.beebot.persistence.management;

import com.beees.beebot.persistence.domain.BotInfoEntity;
import com.beees.beebot.persistence.repository.BotInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotInfoManager {
    private final BotInfoRepo botInfoRepo;

    public BotInfoEntity getBotInfo(String username){
        return botInfoRepo.findByUsername(username);
    }
}
