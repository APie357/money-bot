package dev.andrewd1.moneybot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class BaseCommand {
    public final String name;
    public final CommandData data;

    public BaseCommand(String name, CommandData data) {
        this.name = name;
        this.data = data;
    }

    public abstract void invoke(SlashCommandInteractionEvent event);
}
