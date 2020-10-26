package com.beees.beebot.persistence.management;

import com.beees.beebot.persistence.domain.polling.PollE;
import com.beees.beebot.persistence.repository.PollRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollManager {

    private final PollRepo pollRepo;

    public void savePoll(PollE poll){
        //TODO this should be unnecessary
        poll.setCreatedAt(ZonedDateTime.now());
        pollRepo.save(poll);
    }

    public List<PollE> getCompletedUnclosedPolls(){
        return pollRepo.findAllByEndTimeBeforeAndClosedFalse(ZonedDateTime.now());
    }
}
