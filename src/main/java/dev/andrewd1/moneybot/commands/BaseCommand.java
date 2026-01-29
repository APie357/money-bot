package dev.andrewd1.moneybot.commands;

import net.dv8tion.jda.api.interactions.commands.Command;

public abstract class BaseCommand {
    public final String name;
    public final Command command;

    public BaseCommand(String name, Command command) {
        this.name = name;
        this.command = command;
    }
}
