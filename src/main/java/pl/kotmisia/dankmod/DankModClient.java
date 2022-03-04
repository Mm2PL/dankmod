package pl.kotmisia.dankmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

public class DankModClient implements ClientModInitializer {
    public static KeyBinding itemToGiveBind;

    @Override
    public void onInitializeClient() {
        DankModClient.itemToGiveBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.dankmod.itemToGive",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F12,
                "category.dankmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (DankModClient.itemToGiveBind.wasPressed()) {
                if (client.player == null) break;
                var stack = client.player.getInventory().getMainHandStack();
                DankModClient.itemToGiveChat(stack);
            }
        });
    }

    public static void itemToGiveChat(ItemStack stack) {
        var client = MinecraftClient.getInstance();
        if (client.player == null) return;

        var text = DankModClient.itemToGive(stack);
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

    public static String itemToGive(ItemStack stack) {
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


}
