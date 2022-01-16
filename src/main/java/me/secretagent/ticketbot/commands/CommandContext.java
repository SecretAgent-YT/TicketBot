package me.secretagent.ticketbot.commands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

public class CommandContext {

    private final SlashCommandEvent event;

    public CommandContext(SlashCommandEvent event) {
        this.event = event;
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public MessageChannel getChannel() {
        return event.getChannel();
    }

    public Member getMember() {
        return event.getMember();
    }

    public OptionMapping getOption(String name) {
        if (event.getOptionsByName(name).size() != 0) {
            return event.getOption(name);
        } else {
            return null;
        }
    }

    public ReplyAction reply(String content, boolean hidden) {
        return event.reply(content).setEphemeral(hidden);
    }

    public ReplyAction reply(MessageEmbed embed, boolean hidden) {
        return event.replyEmbeds(embed).setEphemeral(hidden);
    }

    public ReplyAction reply(String content) {
        return reply(content, true);
    }

    public ReplyAction reply(MessageEmbed embed) {
        return reply(embed, true);
    }

    public User getUser() {
        return getMember().getUser();
    }

    public SlashCommandEvent getEvent() {
        return event;
    }

}
