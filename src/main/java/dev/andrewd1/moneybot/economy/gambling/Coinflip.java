package dev.andrewd1.moneybot.economy.gambling;

import dev.andrewd1.moneybot.Bot;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

public class Coinflip {
    private static final Random random = new Random();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Member initiator;
    private final Member opponent;
    private final int bet;

    public Button createButton() {
        random.setSeed(Instant.now().toEpochMilli());
        return Bot.instance.getButtonManager().makeButton("Accept", this::execute);
    }

    public Coinflip(Member initiator, Member opponent, int bet) {
        this.initiator = initiator;
        this.opponent = opponent;
        this.bet = bet;
    }

    private boolean execute(ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getMember()).getIdLong() != opponent.getIdLong()) {
            event.reply("Only the opponent can accept a bet!").setEphemeral(true).queue();
            return false;
        }

        var economy = Bot.instance.getEconomy();
        logger.info("Executing coinflip for ${}: {} vs {}", bet, initiator.getUser().getName(), opponent.getUser().getName());

        try {
            if (!economy.hasEnoughMoney(initiator, bet)) {
                event.reply(initiator.getAsMention() + " doesn't have enough money!").queue();
                return false;
            }

            if (!economy.hasEnoughMoney(opponent, bet)) {
                event.reply(opponent.getAsMention() + " doesn't have enough money!").queue();
                return false;
            }

            var flip = random.nextInt(100) % 2 == 0;

            var winner = flip ? initiator : opponent;
            var loser =  flip ? opponent : initiator;
            economy.addMoney(winner, bet);
            economy.removeMoney(loser, bet);

            event.reply(winner.getAsMention() + " won a coinflip against " + loser.getAsMention() + " for `$" + bet + "`!").queue();
            return true;
        } catch (SQLException e) {
            event.reply("Internal error").queue();
            logger.trace("SQL Error: ", e);
        }

        return false;
    }
}
