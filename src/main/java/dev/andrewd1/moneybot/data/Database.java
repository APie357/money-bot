package dev.andrewd1.moneybot.data;

import dev.andrewd1.moneybot.Bot;
import dev.andrewd1.moneybot.Globals;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Database {
    public final Connection connection;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Database() throws SQLException {
        logger.info("Loading database");
        connection = DriverManager.getConnection("jdbc:h2:./data");
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        logger.info("Connected to database");

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS money (userid BIGINT, guildid BIGINT, amount INTEGER)");
        logger.info("Created tables");
    }

    @Deprecated
    public void initUsers() {
        for (var guild : Bot.instance.getJDA().getGuilds()) {
            logger.info("Initializing {}", guild.getName());
            guild.loadMembers((Member member) -> {
                if (member.getUser().isBot()) return;
                initUser(member);
            });
        }
    }

    public void initUser(Member member) {
        try {
            var statement = connection.prepareStatement("INSERT INTO money (userid, guildid, amount) SELECT ?, ?, ? WHERE NOT EXISTS ( SELECT 1 FROM money WHERE userid = ? AND guildid = ? );");
            statement.setLong(1, member.getIdLong());
            statement.setLong(2, member.getGuild().getIdLong());
            statement.setInt(3, Globals.INITIAL_MONEY);
            statement.setLong(4, member.getIdLong());
            statement.setLong(5, member.getGuild().getIdLong());
            statement.execute();
        } catch (SQLException e) {
            logger.trace("Error while initiating users: ", e);
        }
    }

    private void close() {
        try {
            connection.close();
            logger.info("Database closed");
        } catch (SQLException _) { }
    }
}
