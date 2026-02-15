package dev.andrewd1.moneybot.buttons;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;
import java.util.UUID;

public class ButtonManager {
    private final HashMap<UUID, CallableButton> buttons = new HashMap<>();

    public ButtonManager() {}

    public Button makeButton(String name, CallableButton callableButton) {
        return makeButton(name, callableButton, ButtonStyle.PRIMARY);
    }

    public Button makeButton(String name, CallableButton callableButton, ButtonStyle style) {
        var uuid = UUID.randomUUID();
        buttons.put(uuid, callableButton);
        return Button.of(style, uuid.toString(), name);
    }

    public void execute(ButtonInteractionEvent event) {
        var uuid = UUID.fromString(event.getCustomId());
        if (!buttons.containsKey(uuid)) {
            event.reply("This interaction doesn't exist.").setEphemeral(true).queue();
            return;
        }

        var button = buttons.get(uuid);
        if (button.execute(event)) {
            buttons.remove(uuid);
        }
    }
}
