package pl.kotmisia.dankmod.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.kotmisia.dankmod.DankModClient;

@Mixin(HandledScreen.class)
public abstract class InventoryMixin {

    @Shadow
    public abstract ScreenHandler getScreenHandler();

    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (DankModClient.itemToGiveBind.matchesKey(keyCode, scanCode)) {
            var handler = (ScreenHandler) this.getScreenHandler();
            var stack = handler.getCursorStack();
            if (stack.isEmpty()) {
                var access = (HandledScreenAccessor) this;
                var slot = access.getFocusedSlot();
                if (slot != null && slot.hasStack()) {
                    stack = slot.getStack();
                }
            }
            if (!stack.isEmpty()) {
                DankModClient.itemToGiveChat(stack);
            }
            cir.setReturnValue(true);
        }
    }
}
