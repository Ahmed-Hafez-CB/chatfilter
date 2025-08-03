package net.thunder.chatfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.thunder.chatfilter.command.ChatFilterCommand;

import java.util.ArrayList;
import java.util.List;

public class ChatFilterClient implements ClientModInitializer {

    private static boolean filterEnabled = true;

    public static List<String> FILTER_PREFIXES = new ArrayList<>(List.of(
            "[broadcast]",
            "[crates]",
            "vote",
            ">> info"
    ));

    private static KeyBinding TOGGLE_KEY;

    private static String stripFormatting(String text) {
        return text.replaceAll("§.", "");
    }

    @Override
    public void onInitializeClient() {
        TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.chatfilter.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.invadedlands"
        ));

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

            String plain = stripFormatting(message.getString()).toLowerCase();

            for (String prefix : FILTER_PREFIXES) {
                if (plain.startsWith(prefix)) {
                    return false; // Block message from being shown
                }
            }

            return true; // Allow message
        });

        ClientCommandRegistrationCallback.EVENT.register(ChatFilterCommand::register);
    }
}
