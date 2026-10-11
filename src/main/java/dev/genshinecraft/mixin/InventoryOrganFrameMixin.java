package dev.genshinecraft.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryOrganFrameMixin {
    //? if >=26.3 {
    /*@Inject(method = "extractBackground", at = @At("TAIL"))
    private void genshinecraft$organFrame(net.minecraft.client.gui.GuiGraphicsExtractor graphics,
                                          int mouseX, int mouseY, float delta, CallbackInfo ci) {
        drawFrame(graphics, ((InventoryFramePositionAccessor) this).genshinecraft$left(),
                ((InventoryFramePositionAccessor) this).genshinecraft$top());
    }

    private static void drawFrame(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int left, int top) {
        int x = left + 157, y = top + 68;
        graphics.fill(x, y, x + 18, y + 18, 0xFF333333);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF858585);
        graphics.fill(x + 1, y + 1, x + 17, y + 2, 0xFFF0F0F0);
        graphics.fill(x + 1, y + 1, x + 2, y + 17, 0xFFF0F0F0);
    }
    *///?} else {
    @Inject(method = "renderBg", at = @At("TAIL"))
    private void genshinecraft$organFrame(net.minecraft.client.gui.GuiGraphics graphics,
                                          float delta, int mouseX, int mouseY, CallbackInfo ci) {
        int x = ((InventoryFramePositionAccessor) this).genshinecraft$left() + 157;
        int y = ((InventoryFramePositionAccessor) this).genshinecraft$top() + 68;
        graphics.fill(x, y, x + 18, y + 18, 0xFF333333);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF858585);
        graphics.fill(x + 1, y + 1, x + 17, y + 2, 0xFFF0F0F0);
        graphics.fill(x + 1, y + 1, x + 2, y + 17, 0xFFF0F0F0);
    }
    //?}
}
