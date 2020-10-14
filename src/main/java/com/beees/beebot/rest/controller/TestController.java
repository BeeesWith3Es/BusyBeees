package com.beees.beebot.rest.controller;


import com.beees.beebot.rest.pojo.SimpleMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/ping")
    public SimpleMessage ping(@RequestParam(value = "reply", defaultValue = "pong") String reply){
        return new SimpleMessage(reply);
    }
}
