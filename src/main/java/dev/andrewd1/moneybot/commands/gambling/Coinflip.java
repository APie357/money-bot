package dev.andrewd1.moneybot.commands.gambling;

import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Coinflip extends BaseCommand {
    public Coinflip() {
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
        event.reply("not implemented yet").setEphemeral(true).queue();
    }
}
