package com.beees.beebot.disco.services;

import com.beees.beebot.persistence.domain.polling.PollE;
import com.beees.beebot.persistence.management.PollManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollManager pollManager;

    @Transactional
    public PollE getPoll(String id){
        return pollManager.getPoll(id);
    }

    public void savePoll(PollE poll){
        pollManager.savePoll(poll);
    }

    public List<PollE> getCompletedUnclosedPolls(){
        return pollManager.getCompletedUnclosedPolls();
    }
}
