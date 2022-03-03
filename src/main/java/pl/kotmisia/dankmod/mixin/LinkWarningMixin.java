package pl.kotmisia.dankmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.kotmisia.dankmod.DankMod;

@Mixin(Screen.class)
public class LinkWarningMixin {
    @Inject(at = @At("HEAD"), method = "renderTextHoverEffect")
    private void renderTextHoverEffect(MatrixStack matrices, Style style, int x, int y, CallbackInfo ci) {
        if (style == null) {
            return;
        }
        ClickEvent event = style.getClickEvent();
        if (event == null) {
            return;
        }
        Screen that = (Screen) (Object) this;

        var cfg = DankMod.getConfig();
        String value = event.getValue();
        ClickEvent.Action action = event.getAction();
        String warnKey = null;
        if (action == ClickEvent.Action.RUN_COMMAND && cfg.warnCommand) {
            warnKey = "dankmod.warn.command";
        } else if (action == ClickEvent.Action.COPY_TO_CLIPBOARD && cfg.warnClipboard) {
            warnKey = "dankmod.warn.clipboard";
        } else if (action == ClickEvent.Action.OPEN_URL && cfg.warnUrl) {
            warnKey = "dankmod.warn.url";
        }

        if (warnKey != null) {
            var text = new TranslatableText(warnKey, value).formatted(Formatting.DARK_RED);
            int width = Math.max(that.width/2, 200);
            var hasHover = style.getHoverEvent() != null;
            if (hasHover) {
                if (x < 200) {
                    width = x;
                    x = 0;
                } else {
                    width = Math.max(that.width / 2, 200);
                    x -= 200;
                }
            }

            that.renderOrderedTooltip(
                    matrices,
                    MinecraftClient.getInstance().textRenderer.wrapLines(
                            text,
                            width
                    ),
                    x,
                    y
            );
        }
    }
}
