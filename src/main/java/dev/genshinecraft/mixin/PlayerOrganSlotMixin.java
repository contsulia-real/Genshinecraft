package dev.genshinecraft.mixin;

import dev.genshinecraft.item.OrganSlotHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?} else {
import net.minecraft.nbt.CompoundTag;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerOrganSlotMixin implements OrganSlotHolder {
    @Unique private SimpleContainer genshinecraft$slot;
    @Unique private static final String GENSHINCRAFT_ORGAN = "genshinecraft.organ";

    @Override
    public SimpleContainer genshinecraft$organSlot() {
        if (genshinecraft$slot == null) {
            genshinecraft$slot = new SimpleContainer(1);
        }
        return genshinecraft$slot;
    }

    //? if >=26.3 {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveOrgan(ValueOutput out, CallbackInfo ci) {
        ItemStack stack = genshinecraft$organSlot().getItem(0);
        if (!stack.isEmpty()) {
            out.store(GENSHINCRAFT_ORGAN, ItemStack.CODEC, stack);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadOrgan(ValueInput in, CallbackInfo ci) {
        genshinecraft$organSlot().setItem(0, in.read(GENSHINCRAFT_ORGAN, ItemStack.CODEC)
                .orElse(ItemStack.EMPTY));
    }
    *///?} else if =1.21.1 {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveOrgan(CompoundTag tag, CallbackInfo ci) {
        ItemStack stack = genshinecraft$organSlot().getItem(0);
        if (!stack.isEmpty()) {
            tag.put(GENSHINCRAFT_ORGAN, stack.save(((Player) (Object) this).registryAccess()));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadOrgan(CompoundTag tag, CallbackInfo ci) {
        genshinecraft$organSlot().setItem(0, ItemStack.parseOptional(
                ((Player) (Object) this).registryAccess(), tag.getCompound(GENSHINCRAFT_ORGAN)));
    }
    //?} else {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveOrgan(CompoundTag tag, CallbackInfo ci) {
        ItemStack stack = genshinecraft$organSlot().getItem(0);
        if (!stack.isEmpty()) {
            tag.put(GENSHINCRAFT_ORGAN, stack.save(new CompoundTag()));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadOrgan(CompoundTag tag, CallbackInfo ci) {
        genshinecraft$organSlot().setItem(0, ItemStack.of(tag.getCompound(GENSHINCRAFT_ORGAN)));
    }
    *///?}
}
