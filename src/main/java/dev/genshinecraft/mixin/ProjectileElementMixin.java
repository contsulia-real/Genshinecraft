package dev.genshinecraft.mixin;

import dev.genshinecraft.combat.ElementalProjectile;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.WeaponBinding;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.server.level.ServerLevel;
//? if >=26.3 {
/*import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
*///?} else {
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
//?}
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
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

import java.util.UUID;

@Mixin(Projectile.class)
public abstract class ProjectileElementMixin implements ElementalProjectile {
    @Unique private UUID genshinecraft$carrierId;
    @Unique private CarrierData.Element genshinecraft$element;

    @Override
    public UUID genshinecraft$carrierId() {
        return genshinecraft$carrierId;
    }

    @Override
    public CarrierData.Element genshinecraft$element() {
        return genshinecraft$element;
    }

    /** Capture the weapon binding when the projectile's player owner is assigned. */
    @Inject(method = "setOwner", at = @At("TAIL"))
    private void genshinecraft$onOwnerAssigned(Entity owner, CallbackInfo ci) {
        Projectile projectile = (Projectile) (Object) this;
        if (!(projectile.level() instanceof ServerLevel) || !(projectile instanceof AbstractArrow)
                || !(owner instanceof Player player)) {
            return;
        }
        boolean trident = projectile instanceof ThrownTrident;
        if (genshinecraft$capture(player.getUseItem(), player, trident)) return;
        if (genshinecraft$capture(player.getMainHandItem(), player, trident)) return;
        genshinecraft$capture(player.getOffhandItem(), player, trident);
    }

    @Unique
    private boolean genshinecraft$capture(ItemStack weapon, Player player, boolean trident) {
        if (trident ? !(weapon.getItem() instanceof TridentItem)
                    : !(weapon.getItem() instanceof BowItem || weapon.getItem() instanceof CrossbowItem)) {
            return false;
        }
        WeaponBinding binding = WeaponBinding.read(weapon);
        if (binding == null || WeaponBinding.resolve(weapon, player) == null) {
            return false;
        }
        genshinecraft$carrierId = binding.carrierId();
        genshinecraft$element = binding.element();
        return true;
    }

    //? if >=26.3 {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$save(ValueOutput output, CallbackInfo ci) {
        if (genshinecraft$element != null && genshinecraft$carrierId != null) {
            output.putString("genshinecraft.element", genshinecraft$element.name());
            output.putString("genshinecraft.carrier", genshinecraft$carrierId.toString());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$load(ValueInput input, CallbackInfo ci) {
        genshinecraft$readSnapshot(input.getStringOr("genshinecraft.element", ""),
                input.getStringOr("genshinecraft.carrier", ""));
    }
    *///?} else {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$save(CompoundTag tag, CallbackInfo ci) {
        if (genshinecraft$element != null && genshinecraft$carrierId != null) {
            tag.putString("genshinecraft.element", genshinecraft$element.name());
            tag.putString("genshinecraft.carrier", genshinecraft$carrierId.toString());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$load(CompoundTag tag, CallbackInfo ci) {
        genshinecraft$readSnapshot(tag.getString("genshinecraft.element"),
                tag.getString("genshinecraft.carrier"));
    }
    //?}

    @Unique
    private void genshinecraft$readSnapshot(String element, String carrier) {
        genshinecraft$element = null;
        genshinecraft$carrierId = null;
        if (element.isEmpty() || carrier.isEmpty()) {
            return;
        }
        try {
            genshinecraft$element = CarrierData.Element.valueOf(element);
            genshinecraft$carrierId = UUID.fromString(carrier);
        } catch (IllegalArgumentException ignored) {
            genshinecraft$element = null;
            genshinecraft$carrierId = null;
        }
    }
}
