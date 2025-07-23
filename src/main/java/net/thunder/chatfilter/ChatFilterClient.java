package net.thunder.chatfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public class ChatFilterClient implements ClientModInitializer {

    private static boolean filterEnabled = true;

    private static final Set<String> FILTER_PREFIXES = Set.of(
            "[broadcast]",
            "[crates]"
    );

    private static final KeyBinding TOGGLE_KEY = new KeyBinding(
            "key.chatfilter.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.chatfilter"
    );

    @Override
    public void onInitializeClient() {

        // Toggle filter with G key
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_KEY.wasPressed()) {
                filterEnabled = !filterEnabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Chat filter " + (filterEnabled ? "enabled ✅" : "disabled ❌")), false);
                }
            }
        });

        // Block server messages starting with defined prefixes
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (!filterEnabled) return true;

            String plain = message.getString().toLowerCase();

            for (String prefix : FILTER_PREFIXES) {
                if (plain.startsWith(prefix)) {
                    return false; // Block message from being shown
                }
            }

            return true; // Allow message
        });
    }
}
