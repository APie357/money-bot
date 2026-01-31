package dev.andrewd1.moneybot.economy;

import dev.andrewd1.moneybot.data.Database;
import net.dv8tion.jda.api.entities.Member;

import java.sql.SQLException;

public class Economy {
    private final Database db;
    
    public Economy(Database db) {
        this.db = db;
    }
    
    public int getMoney(Member member) throws SQLException {
        if (member.getUser().isBot()) {
            return -1;
        }

        var statement = db.connection.prepareStatement("SELECT * FROM money WHERE userid = ? AND guildid = ? LIMIT 1;");
        statement.setLong(1, member.getIdLong());
        statement.setLong(2, member.getGuild().getIdLong());
        var result =  statement.executeQuery();

        if (result.next()) {
            return result.getInt("amount");
        }

        db.initUser(member);
        return getMoney(member);
    }

    public void setMoney(Member member, int amount) throws SQLException {
        var statement = db.connection.prepareStatement("MERGE INTO money (userid, guildid, amount) KEY (userid, guildid) VALUES (?, ?, ?);");
        statement.setLong(1, member.getIdLong());
        statement.setLong(2, member.getGuild().getIdLong());
        statement.setInt(3, amount);
        statement.execute();
    }

    public void addMoney(Member member, int amount) throws SQLException {
        setMoney(member, getMoney(member) + amount);
    }

    public void removeMoney(Member member, int amount) throws SQLException {
        addMoney(member, -amount);
    }

    public boolean hasEnoughMoney(Member member, int amount) throws SQLException {
        return getMoney(member) >= amount;
    }
}
