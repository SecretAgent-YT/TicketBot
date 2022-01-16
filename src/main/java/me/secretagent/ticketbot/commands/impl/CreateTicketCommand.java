package me.secretagent.ticketbot.commands.impl;

import me.secretagent.ticketbot.TicketBot;
import me.secretagent.ticketbot.commands.Command;
import me.secretagent.ticketbot.commands.CommandContext;
import me.secretagent.ticketbot.ticket.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class CreateTicketCommand extends Command implements EventListener {

    private final TicketBot ticketBot;

    public CreateTicketCommand(TicketBot ticketBot) {
        this.ticketBot = ticketBot;
        ticketBot.getJDA().addEventListener(this);
    }

    @Override
    public CommandData getData() {
        return new CommandData("createticket", "Menu for creating a ticket");
    }

    @Override
    public void onCalled(CommandContext context) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Haha")
                .setDescription("ur mother");
        List<Button> buttonList = new ArrayList<>();
        int i = 0;
        for (Object o : (JSONArray) ticketBot.getConfig().get("buttons")) {
            buttonList.add(Button.primary("createTicket" + i, o.toString()));
            i++;
        }
        context.reply(builder.build()).addActionRow(buttonList).setEphemeral(true).queue();
    }


    @Override
    public void onEvent(@NotNull GenericEvent genericEvent) {
        if (genericEvent instanceof ButtonClickEvent) {
            ButtonClickEvent event = (ButtonClickEvent) genericEvent;
            if (event.getButton().getId().contains("createTicket")) {
                Ticket.createTicket(event.getMember(), ticketBot, ticket -> {
                    event.reply("You already have a ticket open!").setEphemeral(true).queue();
                });
                if (!event.isAcknowledged()) {
                    event.reply("Created your ticket!").setEphemeral(true).queue();
                }
            }
        }
    }

}
