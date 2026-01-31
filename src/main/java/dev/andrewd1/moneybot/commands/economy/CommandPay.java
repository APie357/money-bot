package dev.andrewd1.moneybot.commands.economy;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class CommandPay extends BaseCommand {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CommandPay() {
        super(
                "pay",
                Commands.slash("pay", "Pay someone money")
                        .setContexts(InteractionContextType.GUILD)
                        .addOption(OptionType.USER, "user", "Whom you want to pay", true)
                        .addOption(OptionType.INTEGER, "amount", "How much you want to pay them", true)
        );
    }

    @Override
    public void invoke(SlashCommandInteractionEvent event) {
        var user = event.getMember();
        var toPay = Objects.requireNonNull(event.getOption("user")).getAsMember();
        var amount = Objects.requireNonNull(event.getOption("amount")).getAsInt();

        if (amount <= 0) {
            event.reply("You can't pay negative money, you thief").setEphemeral(true).queue();
            return;
        }

        assert user != null;
        assert toPay != null;
        if (user.getIdLong() == toPay.getIdLong()) {
            event.reply("You can't pay yourself").setEphemeral(true).queue();
            return;
        }

        if (user.getUser().isBot() || toPay.getUser().isBot()) {
            event.reply("You can't pay a bot").setEphemeral(true).queue();
            return;
        }


        try {
            var userMoney = Bot.instance.getEconomy().getMoney(user);

            if (userMoney < amount) {
                event.reply("You don't have enough money").setEphemeral(true).queue();
            }

            Bot.instance.getEconomy().removeMoney(user, amount);
            Bot.instance.getEconomy().addMoney(toPay, amount);

            event.reply(user.getAsMention() + " paid " + toPay.getAsMention() + " `$" + amount + "`").queue();
            logger.info("{} paid {} ${}", user.getUser().getName(), toPay.getUser().getName(), amount);
        } catch (SQLException e) {
            event.reply("Internal SQL error").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
