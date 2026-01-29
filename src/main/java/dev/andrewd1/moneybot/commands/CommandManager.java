package dev.andrewd1.moneybot.commands;

import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, BaseCommand> commands = new HashMap<>();

    public void registerCommands() {

    }

    public void registerCommand(BaseCommand command) {
        commands.put(command.name, command);
    }
}
