package dev.andrewd1.moneybot.commands.gambling;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.commands.BaseCommand;
import dev.andrewd1.moneybot.economy.gambling.Coinflip;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.SQLException;
import java.util.Objects;

public class CommandCoinflip extends BaseCommand {
    public CommandCoinflip() {
        super(
                "coinflip",
                Commands.slash("coinflip", "Flip a coin")
                        .setContexts(InteractionContextType.GUILD)
                        .addOption(OptionType.USER, "user", "Your opponent", true)
                        .addOption(OptionType.INTEGER, "amount", "How much you want to bet", true)
        );
    }

    @Override
    public void invoke(SlashCommandInteractionEvent event) {
        var economy = Bot.instance.getEconomy();
        var initiator = event.getMember();
        var opponent = Objects.requireNonNull(event.getOption("user")).getAsMember();
        var bet = Objects.requireNonNull(event.getOption("amount")).getAsInt();

        assert initiator != null;
        assert opponent != null;

        if (initiator.getId().equals(opponent.getId())) {
            event.reply("You can't do a coinflip against yourself!").setEphemeral(true).queue();
            return;
        }

        if (initiator.getUser().isBot() || opponent.getUser().isBot()) {
            event.reply("Can't do a coinflip against a bot!").setEphemeral(true).queue();
            return;
        }

        if (bet <= 0) {
            event.reply("Can't do a bet for a negatibe amount of money!").setEphemeral(true).queue();
        }

        try {
            if (!economy.hasEnoughMoney(initiator, bet)) {
                event.reply(initiator.getAsMention() + " doesn't have enough money!").setEphemeral(true).queue();
                return;
            }

            if (!economy.hasEnoughMoney(opponent, bet)) {
                event.reply(opponent.getAsMention() + " doesn't have enough money!").setEphemeral(true).queue();
                return;
            }

            var coinflip = new Coinflip(
                    initiator,
                    opponent,
                    bet
            );

            event.reply(initiator.getAsMention() + " has challenged " + opponent.getAsMention() + " to a coinflip for `$" + bet + "`!").addComponents(
                    ActionRow.of(coinflip.createButton())
            ).queue();

        } catch (SQLException e) {
            event.reply("Internal error").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
