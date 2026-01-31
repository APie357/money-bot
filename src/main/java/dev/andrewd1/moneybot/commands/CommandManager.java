package dev.andrewd1.moneybot.commands;

import dev.andrewd1.moneybot.commands.economy.*;
import dev.andrewd1.moneybot.commands.gambling.CommandCoinflip;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class CommandManager {
    private final HashMap<String, BaseCommand> commands = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CommandManager(JDA jda) {
        registerCommands();

        jda.updateCommands()
                .addCommands(
                        commands.values()
                                .stream()
                                .map(cmd -> cmd.data)
                                .toList()
                )
                .queue();
    }

    private void registerCommands() {
        registerCommand(new CommandBalance());
        registerCommand(new CommandBaltop());
        registerCommand(new CommandPay());

        registerCommand(new CommandCoinflip());
    }

    public void invoke(SlashCommandInteractionEvent event) {
        if (commands.containsKey(event.getName())) {
            var command = commands.get(event.getName());
            logger.info("Command {} invoked by {}", command.name, event.getUser().getName());
            command.invoke(event);
        }
    }

    private void registerCommand(BaseCommand command) {
        commands.put(command.name, command);
        logger.info("Registered {}", command.getClass().getName());
    }
}
