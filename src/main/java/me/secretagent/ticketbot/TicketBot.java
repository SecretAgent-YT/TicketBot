package me.secretagent.ticketbot;

import me.secretagent.ticketbot.commands.CommandManager;
import me.secretagent.ticketbot.commands.impl.CloseTicketCommand;
import me.secretagent.ticketbot.commands.impl.CreateTicketCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class TicketBot extends ListenerAdapter {

    private JDA jda;
    private final JSONObject config;
    private final String token;

    public TicketBot(JSONObject config) {
        this.config = config;
        this.token = config.get("token").toString();
        try {
            start();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private void start() throws LoginException {
        jda = JDABuilder.createDefault(token)
                .setActivity(Activity.listening("to tickets"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(this)
                .build();
    }

    public static void main(String[] args) throws IOException, ParseException {
        File file = new File("config.json");
        file.createNewFile();
        Scanner scanner = new Scanner(file);
        String inline = "";
        while (scanner.hasNextLine()) {
            inline += scanner.nextLine();
        }
        JSONObject object = (JSONObject) new JSONParser().parse(inline);
        new TicketBot(object);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        CommandManager manager = new CommandManager(jda);
        manager.registerCommand(new CreateTicketCommand(this));
        manager.registerCommand(new CloseTicketCommand());
    }

    public JDA getJDA() {
        return jda;
    }

    public JSONObject getConfig() {
        return config;
    }

}
