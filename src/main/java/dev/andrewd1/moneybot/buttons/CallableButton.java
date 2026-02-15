package dev.andrewd1.moneybot.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface CallableButton {
    boolean execute(ButtonInteractionEvent event);
}
