package com.beees.beebot.disco.services;

import com.beees.beebot.disco.config.JdaStarter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleService {

    public Role getRole(Guild guild, String name){
        List<Role> roles = guild.getRolesByName(name, true);
        if(!roles.isEmpty()){
           return roles.get(0);
        }
        else return null;
    }

    public boolean roleExists(Guild guild, String name){
       return !guild.getRolesByName(name, true).isEmpty();
    }
}
