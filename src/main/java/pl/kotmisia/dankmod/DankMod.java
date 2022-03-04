package pl.kotmisia.dankmod;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kotmisia.dankmod.config.DankCfg;

public class DankMod implements ModInitializer, ClientModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("dankmod");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        AutoConfig.register(DankCfg.class, GsonConfigSerializer::new);
    }

    private static KeyBinding itemToGiveBind;

    @Override
    public void onInitializeClient() {
        DankMod.itemToGiveBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dankmod.itemToGive",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F12,
                "category.dankmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (DankMod.itemToGiveBind.wasPressed()) {
                if (client.player == null) break;
                var stack = client.player.getInventory().getMainHandStack();
                var text = this.itemToGive(stack);
                String userText = text;
                if (text.length() > 100) {
                    userText = text.substring(0, 97) + "...";
                }
                var chatTxt = new TranslatableText("gui.dankmod.copyItem", userText)
                        .setStyle(
                                Style.EMPTY
                                        .withClickEvent(
                                                new ClickEvent(
                                                        ClickEvent.Action.COPY_TO_CLIPBOARD, text
                                                )
                                        )
                                        .withColor(Formatting.DARK_AQUA)
                        );


                client.player.sendMessage(chatTxt, false);
            }
        });
    }

    private String itemToGive(ItemStack stack) {
        var namespacedId = Registry.ITEM.getId(stack.getItem());
        var nbtText = "";
        if (stack.hasNbt()) {
            // stack.hasNbt ensures that nbt may not be null.

            //noinspection ConstantConditions
            nbtText = stack.getNbt().asString().replace("\n", "\\n");
        }
        var command = "/give @s " + namespacedId.toString() + nbtText + " " + stack.getCount();
        return command;
    }


    public static DankCfg getConfig() {
        return AutoConfig.getConfigHolder(DankCfg.class).getConfig();
    }

}
