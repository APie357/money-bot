package dev.andrewd1.moneybot.data;

import dev.andrewd1.moneybot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.sql.*;

public class Database {
    public final Connection connection;

    public Database() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:./data");
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS money (userid BIGINT, guildid BIGINT, interacted BOOLEAN, amount INTEGER)");
    }

    public void initUsers() {
        for (var guild : Bot.instance.getJDA().getGuilds()) {
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
            System.out.println("Error while initiating users: " + e.getMessage());
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException _) { }
    }
}
