package dev.andrewd1.moneybot.commands.economy;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.SQLException;

public class CommandBalance extends BaseCommand {
    public CommandBalance() {
        super(
                "balance",
                Commands.slash("balance", "Get the balance of a user")
                        .addOption(OptionType.USER, "user", "Whom you want to get the balance of", false)
                        .setContexts(InteractionContextType.GUILD)
        );
    }

    @Override
    public void invoke(SlashCommandInteractionEvent event) {
        Member member = null;

        var memberOption = event.getOption("user");
        if (memberOption != null) {
            member = memberOption.getAsMember();
        }

        if (member == null) {
            member = event.getMember();
        }

        assert member != null;

        if (member.getUser().isBot()) {
            event.reply("You can't check the balance of a bot").setEphemeral(true).queue();
            return;
        }

        try {
            var amount = Bot.instance.getEconomy().getMoney(member);

            if (amount != -1) {
                event.reply(member.getAsMention() + " has `$" + amount + "`").queue();
            } else {
                event.reply(member.getAsMention() + " couldn't be found in the database.").setEphemeral(true).queue();
            }
        } catch (SQLException e) {
            event.reply("Internal SQL error").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
