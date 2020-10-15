package com.beees.beebot.disco.services;

import antlr.debug.MessageEvent;
import com.beees.beebot.disco.config.BotProps;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final BotProps botProps;
    private final MessageService messageService;

    public void colorCommand(List<String> args, MessageReceivedEvent e){
        Map<String, Integer> allowedRoles = new HashMap<>();
        for(String s : botProps.getPermissions().getCanManage()){
            allowedRoles.put(s, 0);
        }
        for(Role r : Objects.requireNonNull(e.getMember()).getRoles()){
            if(allowedRoles.get(r.getName()) != null){
                executeColor(args, e);
                return;
            }
        }
        messageService.reactError(e.getMessage());
    }

    private void executeColor(List<String> args, MessageReceivedEvent e){
        Guild guild = e.getGuild();
        if(args.size() == 2){
            //TODO Color someone else
        }else if(args.size() == 1){
            String hexCode = args.get(0);
            if(!hexCode.startsWith("#") || !(hexCode.length() == 7)){
                messageService.reactError(e.getMessage());
                return;
            }
            Role colorRole = null;
            for(Role r : guild.getRoles()){
                if(hexCode.equals(r.getName())){
                    colorRole = r;
                    break;
                }
            }
            if(colorRole == null){
                RoleAction pendingRole = guild.createRole();
                int color = 0;
                try{
                    color = Integer.decode(hexCode);
                }catch(Exception ex){
                    messageService.reactError(e.getMessage());
                    return;
                }
                pendingRole.setColor(color).setName(hexCode).queue((newRole)->{
                    applyColor(newRole, e);
                    messageService.reactSuccess(e.getMessage());
                });
                return;
            }else{
                applyColor(colorRole, e);
                messageService.reactSuccess(e.getMessage());
                return;
            }
        }
        messageService.reactError(e.getMessage());
    }

    private void applyColor(Role role, MessageReceivedEvent e){
        Member member = e.getMember();
        Guild guild = e.getGuild();
        for(Role r : member.getRoles()){
            if(r.getName().startsWith("#")){
                guild.removeRoleFromMember(member, r).queue();
            }
        }
        guild.addRoleToMember(Objects.requireNonNull(member), role).queue();
    }
}
