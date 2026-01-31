package dev.andrewd1.moneybot.economy.gambling;

import dev.andrewd1.moneybot.Bot;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.sql.SQLException;
import java.util.*;

public class Coinflip {
    private static final HashMap<UUID, Coinflip> coinflips = new HashMap<>();
    private static final Random random = new Random();

    private final Member initiator;
    private final Member opponent;
    private final int bet;

    public Button createButton() {
        var uuid = UUID.randomUUID();
        coinflips.put(uuid, this);
        random.setSeed(uuid.hashCode());
        return Button.success(uuid.toString(), "Accept");
    }

    public Coinflip(Member initiator, Member opponent, int bet) {
        this.initiator = initiator;
        this.opponent = opponent;
        this.bet = bet;
    }

    public static void executeIfExists(ButtonInteractionEvent event) {
        var uuid = UUID.fromString(event.getCustomId());
        if (coinflips.containsKey(uuid)) {
            var coinflip = coinflips.get(uuid);
            if (Objects.requireNonNull(event.getMember()).getIdLong() != coinflip.opponent.getIdLong()) {
                event.reply("Only the opponent can accept a bet!").setEphemeral(true).queue();
                return;
            }

            coinflip.execute(event);
            coinflips.remove(uuid);
        } else {
            event.reply("Couldn't find the coinflip. Did you already perform the bet?").setEphemeral(true).queue();
        }
    }

    private void execute(ButtonInteractionEvent event) {
        var economy = Bot.instance.getEconomy();

        try {
            if (!economy.hasEnoughMoney(initiator, bet)) {
                event.reply(initiator.getAsMention() + " doesn't have enough money!").queue();
                return;
            }

            if (!economy.hasEnoughMoney(opponent, bet)) {
                event.reply(opponent.getAsMention() + " doesn't have enough money!").queue();
                return;
            }

            var flip = random.nextInt(100) % 2 == 0;

            var winner = flip ? initiator : opponent;
            var loser =  flip ? opponent : initiator;
            economy.addMoney(winner, bet);
            economy.removeMoney(loser, bet);

            event.reply(winner.getAsMention() + " won a coinflip against " + loser.getAsMention() + " for `$" + bet + "`!").queue();
        } catch (SQLException e) {
            event.reply("Internal error").queue();
            e.printStackTrace();
        }
    }
}
