package me.secretagent.ticketbot.commands.impl;

import me.secretagent.ticketbot.commands.Command;
import me.secretagent.ticketbot.commands.CommandContext;
import me.secretagent.ticketbot.ticket.Ticket;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class CloseTicketCommand extends Command {

    @Override
    public CommandData getData() {
        return new CommandData("closeticket", "Closes a ticket");
    }

    @Override
    public void onCalled(CommandContext context) {
        Member member = context.getMember();
        if (!member.hasPermission(Permission.MANAGE_PERMISSIONS)) {
            context.reply("You don't have permission to do that!", true).queue();
            return;
        }
        for (Ticket ticket : Ticket.getTickets()) {
            if (ticket.getChannel().getId().equals(context.getChannel().getId())) {
                context.reply("Closing ticket...", true).queue();
                ticket.close();
                return;
            }
        }
        context.reply("You aren't in a ticket channel!", true).queue();
    }

}
