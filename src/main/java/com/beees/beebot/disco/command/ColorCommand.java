package com.beees.beebot.disco.command;

import com.beees.beebot.disco.config.BotProps;
import com.beees.beebot.disco.services.MessageService;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ColorCommand extends Command{

    private static Command command;
    private final BotProps botProps;
    private final MessageService messageService;

    @PostConstruct
    public void init(){
        command = this;
        this.name = "color";
        this.aliases = new String[]{"gib color"};
        this.help = "args: #FFFFFF user? \r Assigns a color of the given hexcode to you or the mentioned user (optional)";

    }

    @Override
    protected void execute(CommandEvent e) {

        List<String> args = messageService.parseArgs(e.getArgs());
        Map<String, Integer> allowedRoles = new HashMap<>();
        for(String s : botProps.getPermissions().getManageLightRoles()){
            allowedRoles.put(s, 0);
        }
        for(Role r : Objects.requireNonNull(e.getMember()).getRoles()){
            if(allowedRoles.get(r.getName()) != null && args.size() > 0 && args.size() <= 2){
                executeColor(args, e);
                return;
            }
        }
        messageService.reactError(e.getMessage());
    }

    public static Command getCommand(){
        return command;
    }

    private void executeColor(List<String> args, CommandEvent e) {
        Guild guild = e.getGuild();
        Member memberToColor = e.getMessage().getMember();
        if(args.size() >=2){
            memberToColor = e.getGuild().retrieveMemberById(args.get(1)).complete();
        }
        if(memberToColor == null){
            messageService.reactError(e.getMessage());
            e.getChannel().sendMessage("Couldn't find the user to color!").queue();
            return;
        }
        String hexCode = args.get(0);
        if (!hexCode.startsWith("#") || !(hexCode.length() == 7)) {
            messageService.reactError(e.getMessage());
            return;
        }
        Role colorRole = null;
        for (Role r : guild.getRoles()) {
            if (hexCode.equals(r.getName())) {
                colorRole = r;
                break;
            }
        }
        if (colorRole == null) {
            RoleAction pendingRole = guild.createRole();
            int color = 0;
            try {
                color = Integer.decode(hexCode);
            } catch (Exception ex) {
                messageService.reactError(e.getMessage());
                return;
            }
            final Member finalMemberToColor = memberToColor;
            pendingRole.setColor(color).setName(hexCode).queue((newRole) -> {
                applyColor(newRole, finalMemberToColor);
                messageService.reactSuccess(e.getMessage());
            });
        } else {
            applyColor(colorRole, memberToColor);
            messageService.reactSuccess(e.getMessage());
        }
    }

    private void applyColor(Role role, Member member){
        Guild guild = member.getGuild();
        for(Role r : member.getRoles()){
            if(r.getName().startsWith("#")){
                guild.removeRoleFromMember(member, r).queue();
            }
        }
        guild.addRoleToMember(Objects.requireNonNull(member), role).queue();
    }
}
