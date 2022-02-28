package pl.kotmisia.dankmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class LinkWarningMixin {
//    @Inject(at = @At("HEAD"), method = "handleTextClick")
//    private void handleTextClick(@Nullable Style style, CallbackInfoReturnable<Boolean> cir) {
//        DankMod.LOGGER.warn("HIT THIS!");
//    }

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

        String value = event.getValue();
        ClickEvent.Action action = event.getAction();
        if (action == ClickEvent.Action.RUN_COMMAND) {
            String text = "WARNING: Clicking this link will run the following command:\n"
                    + value;
            that.renderOrderedTooltip(
                    matrices,
                    MinecraftClient.getInstance().textRenderer.wrapLines(
                            StringVisitable.plain(text), Math.max(that.width / 2, 200)
                    ),
                    x,
                    y
            );
        }
    }
}
