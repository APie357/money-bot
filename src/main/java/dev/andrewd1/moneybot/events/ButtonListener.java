package dev.andrewd1.moneybot.events;

import dev.andrewd1.moneybot.economy.gambling.Coinflip;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Coinflip.executeIfExists(event);
    }
}
