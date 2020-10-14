package com.beees.beebot.disco.config;

import ch.qos.logback.classic.BasicConfigurator;
import com.beees.beebot.disco.commands.BuzzCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;


@Component
@RequiredArgsConstructor
public class JdaStarter {
    private static JDA jda;


    private static String token;

    @Value("${jda.token}")
    public void setToken(String token){
        if(JdaStarter.token == null){
            JdaStarter.token = token;
        }
    }

    public static JDA getJda(){
        return jda;
    }

    public static void start(){
        //Create Bot
        JDABuilder jdaBuilder = JDABuilder.createDefault(token);
        //Register commands
        jdaBuilder.addEventListeners(new BuzzCommand());
        try{
            //Try to log in bot
            jda = jdaBuilder.build();
        }catch (LoginException e){
            e.printStackTrace();
        }
    }
}