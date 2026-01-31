package dev.andrewd1.moneybot.commands.economy;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.sql.SQLException;
import java.util.Objects;

public class CommandBaltop extends BaseCommand {
    public CommandBaltop() {
        super(
                "baltop",
                Commands.slash("baltop", "Get the top 10 people's balance")
                        .setContexts(InteractionContextType.GUILD)
        );
    }

    @Override
    public void invoke(SlashCommandInteractionEvent event) {
        try {
            var statement = Bot.instance.getDatabase().connection.prepareStatement("SELECT * FROM money WHERE guildid = ? ORDER BY amount DESC LIMIT 10;");
            statement.setLong(1, Objects.requireNonNull(event.getGuild()).getIdLong());
            var result =  statement.executeQuery();

            var output = new StringBuilder();
            var empty = true;

            for (int i = 1; result.next(); i++) {
                empty = false;
                output.append(i).append(". <@").append(result.getLong("userid")).append(">: `$").append(result.getInt("amount")).append("`\n");
            }

            if (empty) {
                output.append("Nobody interacted with the bot yet.");
            }

            event.reply(output.toString()).queue();
        } catch (SQLException e) {
            event.reply("Internal SQL error").setEphemeral(true).queue();
            e.printStackTrace();
        }
    }
}
