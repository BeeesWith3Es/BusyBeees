package com.beees.beebot.disco.services;

import com.beees.beebot.persistence.management.BotInfoManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotInfoService {

    private final BotInfoManager botInfoManager;


}
