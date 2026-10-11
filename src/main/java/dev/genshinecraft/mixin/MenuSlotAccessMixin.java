package dev.genshinecraft.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContainerMenu.class)
public interface MenuSlotAccessMixin {
    @Invoker("addSlot")
    Slot genshinecraft$addOrganSlot(Slot slot);
}
