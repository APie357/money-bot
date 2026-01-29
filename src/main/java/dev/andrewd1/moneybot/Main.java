package dev.andrewd1.moneybot;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.EnumSet;

public class Main {
    static void main() {
        Dotenv env = Dotenv.load();
        JDA jda = JDABuilder.createLight(env.get("TOKEN"), EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners()
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("balance", "Get a user's balance")
                        .addOption(OptionType.USER, "user", "Who's balance to get", false)
        );

        commands.queue();
    }
}
