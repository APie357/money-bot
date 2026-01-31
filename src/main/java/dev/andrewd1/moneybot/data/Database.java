package dev.andrewd1.moneybot.data;

import dev.andrewd1.moneybot.Bot;
import net.dv8tion.jda.api.entities.Guild;
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

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS money (userid BIGINT, guildid BIGINT, interacted BOOLEAN, amount INTEGER)");
        logger.info("Created tables");
    }

    public void initUsers() {
        for (var guild : Bot.instance.getJDA().getGuilds()) {
            logger.info("Initializing {}", guild.getName());
            guild.loadMembers((Member member) -> {
                if (member.getUser().isBot()) return;
                initUser(member, guild);
            });
        }
    }

    private void initUser(Member member, Guild guild) {
        try {
            var statement = connection.prepareStatement("INSERT INTO money (userid, guildid, interacted, amount) SELECT ?, ?, false, ? WHERE NOT EXISTS ( SELECT 1 FROM money WHERE userid = ? AND guildid = ? );");
            statement.setLong(1, member.getIdLong());
            statement.setLong(2, guild.getIdLong());
            statement.setInt(3, 5000);
            statement.setLong(4, member.getIdLong());
            statement.setLong(5, guild.getIdLong());
            statement.execute();
        } catch (SQLException e) {
            logger.error("Error while initiating users: {}", e.getMessage());
        }
    }

    private void close() {
        try {
            connection.close();
            logger.info("Database closed");
        } catch (SQLException _) { }
    }
}
