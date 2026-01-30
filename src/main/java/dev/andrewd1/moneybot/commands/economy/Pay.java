package dev.andrewd1.moneybot.commands.economy;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.SQLException;
import java.util.Objects;

public class Pay extends BaseCommand {
    public Pay() {
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
            var getUserMoney = Bot.instance.getDatabase().connection.prepareStatement("SELECT * FROM money WHERE userid = ? AND guildid = ? LIMIT 1;");
            getUserMoney.setLong(1, user.getIdLong());
            getUserMoney.setLong(2, Objects.requireNonNull(event.getGuild()).getIdLong());
            var getUserMoneyResult = getUserMoney.executeQuery();
            if (!getUserMoneyResult.next()) {
                event.reply("You aren't in the database").setEphemeral(true).queue();
                return;
            }

            if (getUserMoneyResult.getInt("amount") - amount < 0) {
                event.reply("You don't have enough money").setEphemeral(true).queue();
                return;
            }

            var updateUserMoney = Bot.instance.getDatabase().connection.prepareStatement("UPDATE money SET amount = amount - ? WHERE userid = ? AND guildid = ?;");
            updateUserMoney.setInt(1, amount);
            updateUserMoney.setLong(2, user.getIdLong());
            updateUserMoney.setLong(3, Objects.requireNonNull(event.getGuild()).getIdLong());
            updateUserMoney.execute();

            var updateToPayMoney = Bot.instance.getDatabase().connection.prepareStatement("UPDATE money SET amount = amount + ? WHERE userid = ? AND guildid = ?;");
            updateToPayMoney.setInt(1, amount);
            updateToPayMoney.setLong(2, toPay.getIdLong());
            updateToPayMoney.setLong(3, Objects.requireNonNull(event.getGuild()).getIdLong());
            updateToPayMoney.execute();

            event.reply(user.getAsMention() + " paid " + toPay.getAsMention() + " `$" + amount + "`").queue();
        } catch (SQLException e) {
            event.reply("Internal SQL error").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
