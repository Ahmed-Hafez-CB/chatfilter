package net.thunder.chatfilter.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import net.thunder.chatfilter.ChatFilterClient;

public class ChatFilterCommand {

    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("chatfilter")
                .then(literal("list")
                        .executes(context -> {
                            var source = context.getSource();
                            var prefixes = ChatFilterClient.FILTER_PREFIXES;

                            if (prefixes.isEmpty()) {
                                source.sendFeedback(Text.literal("No filter prefixes set."));
                            } else {
                                for (int i = 0; i < prefixes.size(); i++) {
                                    source.sendFeedback(Text.literal("[" + i + "] " + prefixes.get(i)));
                                }
                            }

                            return 1;
                        }))
                .then(literal("remove")
                        .then(argument("index", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    int index = IntegerArgumentType.getInteger(context, "index");
                                    var prefixes = ChatFilterClient.FILTER_PREFIXES;

                                    if (index >= 0 && index < prefixes.size()) {
                                        String removed = prefixes.remove(index);
                                        context.getSource().sendFeedback(Text.literal("Removed prefix: " + removed));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("Invalid index."));
                                    }

                                    return 1;
                                })
                        )
                )
                .then(literal("add")
                        .then(argument("prefix", StringArgumentType.string())
                                .executes(context -> {
                                    String prefix = StringArgumentType.getString(context, "prefix");
                                    ChatFilterClient.FILTER_PREFIXES.add(prefix);
                                    context.getSource().sendFeedback(Text.literal("Added prefix: " + prefix));
                                    return 1;
                                })
                        )
                )
        );
    }
}
