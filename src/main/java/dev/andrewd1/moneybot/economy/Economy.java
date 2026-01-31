package dev.andrewd1.moneybot.economy;

import dev.andrewd1.moneybot.Bot;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;

public class Economy {
    public Economy() {}

    public int getMoney(Member member) throws SQLException {
        var statement = Bot.instance.getDatabase().connection.prepareStatement("SELECT * FROM money WHERE userid = ? AND guildid = ? LIMIT 1;");
        statement.setLong(1, member.getIdLong());
        statement.setLong(2, member.getGuild().getIdLong());
        var result =  statement.executeQuery();

        if (result.next()) {
            return result.getInt("amount");
        }

        return -1;
    }

    public void setMoney(Member member, int amount) throws SQLException {
        var statement = Bot.instance.getDatabase().connection.prepareStatement("UPDATE money SET amount = ?, interacted = true WHERE userid = ? AND guildid = ?;");
        statement.setInt(1, amount);
        statement.setLong(2, member.getIdLong());
        statement.setLong(3, member.getGuild().getIdLong());
        statement.execute();
    }

    public void addMoney(Member member, int amount) throws SQLException {
        var statement = Bot.instance.getDatabase().connection.prepareStatement("UPDATE money SET amount = amount + ?, interacted = true WHERE userid = ? AND guildid = ?;");
        statement.setInt(1, amount);
        statement.setLong(2, member.getIdLong());
        statement.setLong(3, member.getGuild().getIdLong());
        statement.execute();
    }

    public void removeMoney(Member member, int amount) throws SQLException {
        addMoney(member, -amount);
    }

    public boolean hasEnoughMoney(Member member, int amount) throws SQLException {
        return getMoney(member) >= amount;
    }
}
