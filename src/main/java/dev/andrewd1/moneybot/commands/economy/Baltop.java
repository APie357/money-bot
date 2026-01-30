package dev.andrewd1.moneybot.commands.economy;

import dev.andrewd1.moneybot.commands.BaseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Baltop extends BaseCommand {
    public Baltop() {
        super("baltop", Commands.slash("baltop", "Get the top 10 people's balance").setContexts(InteractionContextType.GUILD));
    }

    @Override
    public void invoke(SlashCommandInteractionEvent event) {
        event.reply("not implemented yet").setEphemeral(true).queue();
    }
}
