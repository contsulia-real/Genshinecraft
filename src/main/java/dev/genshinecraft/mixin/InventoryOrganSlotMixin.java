package dev.genshinecraft.mixin;

import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.item.OrganSlotHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryOrganSlotMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void genshinecraft$appendOrganSlot(Inventory inventory, boolean active,
                                                Player player, CallbackInfo ci) {
        // Append after vanilla slots: all existing slot indices remain unchanged.
        Slot organ = new Slot(((OrganSlotHolder) player).genshinecraft$organSlot(), 0, 158, 69) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CarrierItems.isCarrier(stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
        ((MenuSlotAccessMixin) this).genshinecraft$addOrganSlot(organ);
    }
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$shiftMoveOrgan(Player player, int slotIndex,
                                               CallbackInfoReturnable<ItemStack> cir) {
        var slots = ((AbstractContainerMenu) (Object) this).slots;
        int organIndex = slots.size() - 1;
        Slot organ = slots.get(organIndex);
        if (slotIndex == organIndex) {
            ItemStack original = organ.getItem();
            if (original.isEmpty()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }
            ItemStack moved = original.copy();
            if (player.getInventory().add(original.copy())) {
                organ.set(ItemStack.EMPTY);
                organ.setChanged();
                cir.setReturnValue(moved);
            } else {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        } else if (slotIndex >= 9 && slotIndex < organIndex
                && organ.getItem().isEmpty()) {
            Slot source = slots.get(slotIndex);
            ItemStack stack = source.getItem();
            if (CarrierItems.isCarrier(stack)) {
                ItemStack previous = stack.copy();
                organ.set(previous);
                source.set(ItemStack.EMPTY);
                organ.setChanged();
                source.setChanged();
                cir.setReturnValue(previous);
            }
        }
    }

}
