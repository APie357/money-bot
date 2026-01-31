package dev.andrewd1.moneybot;

import dev.andrewd1.moneybot.commands.CommandManager;
import dev.andrewd1.moneybot.data.Database;
import dev.andrewd1.moneybot.economy.Economy;
import dev.andrewd1.moneybot.events.ButtonListener;
import dev.andrewd1.moneybot.events.CommandListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.EnumSet;

public class Bot {
    public static Bot instance;
    private final JDA jda;
    private final CommandManager commandManager;
    private final Economy economy;
    private final Database database;

    public Bot() {
        var env = Dotenv.load();
        try {
            database = new Database();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        jda = JDABuilder.create(
                        env.get("TOKEN"),
                        EnumSet.of(
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS
                        )
                )
                .disableCache(
                        CacheFlag.ACTIVITY,
                        CacheFlag.VOICE_STATE,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ONLINE_STATUS,
                        CacheFlag.SCHEDULED_EVENTS
                )
                .addEventListeners(new CommandListener())
                .addEventListeners(new ButtonListener())
                .addEventListeners(new ListenerAdapter() {
                    @Override
                    public void onReady(@NotNull ReadyEvent event) {
                        Bot.this.onReady(event);
                    }
                })
                .setActivity(Activity.of(Activity.ActivityType.PLAYING, "/coinflip"))
                .build();

        commandManager = new CommandManager(jda);
        economy = new Economy();
    }

    private void onReady(ReadyEvent event) {
        instance = this;
        database.initUsers();
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Database getDatabase() {
        return database;
    }

    public JDA getJDA() {
        return jda;
    }

    public Economy getEconomy() {
        return economy;
    }

    static void main() {
        new Bot();
    }
}
