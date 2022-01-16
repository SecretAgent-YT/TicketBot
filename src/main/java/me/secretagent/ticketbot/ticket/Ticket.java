package me.secretagent.ticketbot.ticket;

import me.secretagent.ticketbot.TicketBot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class Ticket implements EventListener {

    private TextChannel channel;
    private final Member member;

    private final UUID uuid = UUID.randomUUID();

    private final TicketBot ticketBot;

    private TicketState ticketState = TicketState.INPUTTING_TITLE;

    private static final List<Ticket> tickets = new ArrayList<>();

    private Ticket(Member member, TicketBot ticketBot) {
        this.member = member;
        this.ticketBot = ticketBot;
        for (Ticket ticket : tickets) {
            if (ticket.getMember().getId().equals(getMember().getId())) {
                close();
                return;
            }
        }
        ticketBot.getJDA()
                .getCategoryById(ticketBot.getConfig().get("category").toString())
                .createTextChannel(member.getEffectiveName() + "-ticket")
                .queue( textChannel -> this.channel = textChannel);
        ticketBot.getJDA().addEventListener(this);
        tickets.add(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (ticketState == TicketState.INPUTTING_TITLE) {
                    close();
                }
            }
        }, 1000 * 60 * 3);
    }

    private Ticket(Member member, TicketBot ticketBot, Consumer<Ticket> consumer) {
        this.member = member;
        this.ticketBot = ticketBot;
        for (Ticket ticket : tickets) {
            if (ticket.getMember().getId().equals(getMember().getId())) {
                consumer.accept(this);
                close();
                return;
            }
        }
        ticketBot.getJDA()
                .getCategoryById(ticketBot.getConfig().get("category").toString())
                .createTextChannel(member.getEffectiveName() + "-ticket")
                .queue( textChannel -> this.channel = textChannel);
        ticketBot.getJDA().addEventListener(this);
        tickets.add(this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (ticketState == TicketState.INPUTTING_TITLE) {
                    close();
                }
            }
        }, 1000 * 60 * 3);
    }

    public void setState(TicketState state) {
        if (state == TicketState.DISCUSSING) {
            getChannel().sendMessage(getChannel().getGuild().getRoleById(ticketBot.getConfig().get("role-to-ping").toString()).getAsMention()).queue();
        }
        this.ticketState = state;
    }

    public void close() {
        setState(TicketState.CLOSED);
        tickets.remove(this);
        if (channel != null) channel.delete().queue();
    }

    public Member getMember() {
        return member;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public static Ticket createTicket(Member member, TicketBot ticketBot) {
        return new Ticket(member, ticketBot);
    }

    public static Ticket createTicket(Member member, TicketBot ticketBot, Consumer<Ticket> consumer) {
        return new Ticket(member, ticketBot, consumer);
    }

    public static List<Ticket> getTickets() {
        return tickets;
    }


    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getTextChannel().getId().equals(getChannel().getId()) && ticketState == TicketState.INPUTTING_TITLE && e.getAuthor().getId().equals(getMember().getId())) {
                setState(TicketState.DISCUSSING);
            }
        }
    }

}
