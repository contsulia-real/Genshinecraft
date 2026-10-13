package dev.genshinecraft.mixin;

import dev.genshinecraft.element.AuraIconHolder;
import dev.genshinecraft.element.ElementalAuraHolder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import dev.genshinecraft.element.ElementalAuraState;
import dev.genshinecraft.element.ElementalReactionEngine;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.world.entity.LivingEntity;
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

@Mixin(LivingEntity.class)
public abstract class LivingElementalAuraMixin implements ElementalAuraHolder, AuraIconHolder {
    @Unique private static final EntityDataAccessor<Integer> GENSHINCRAFT_AURA_ICONS =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    @Unique private static final EntityDataAccessor<String> GENSHINCRAFT_BOSS_BAR =
            SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.STRING);

    //? if =1.20.1 {
    /*@Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void genshinecraft$defineAuraIcons(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getEntityData().define(GENSHINCRAFT_AURA_ICONS, 0);
        ((LivingEntity) (Object) this).getEntityData().define(GENSHINCRAFT_BOSS_BAR, "");
    }
    *///?} else {
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void genshinecraft$defineAuraIcons(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(GENSHINCRAFT_AURA_ICONS, 0);
        builder.define(GENSHINCRAFT_BOSS_BAR, "");
    }
    //?}

    @Override
    public int genshinecraft$auraIcons() {
        return ((LivingEntity) (Object) this).getEntityData().get(GENSHINCRAFT_AURA_ICONS);
    }

    @Override
    public void genshinecraft$setAuraIcons(int mask) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getEntityData().get(GENSHINCRAFT_AURA_ICONS) != mask) {
            entity.getEntityData().set(GENSHINCRAFT_AURA_ICONS, mask);
        }
    }
    @Override
    public String genshinecraft$bossBarId() {
        return ((LivingEntity) (Object) this).getEntityData().get(GENSHINCRAFT_BOSS_BAR);
    }

    @Override
    public void genshinecraft$setBossBarId(String id) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.getEntityData().get(GENSHINCRAFT_BOSS_BAR).equals(id)) {
            entity.getEntityData().set(GENSHINCRAFT_BOSS_BAR, id);
        }
    }

    @Unique private static final String GENSHINCRAFT_AURAS = "genshinecraft.auras";
    @Unique private static final String GENSHINCRAFT_REACTIONS = "genshinecraft.reactions";
    @Unique private final ElementalAuraState genshinecraft$auraState = new ElementalAuraState();

    @Override
    public ElementalAuraState genshinecraft$auras() {
        return genshinecraft$auraState;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void genshinecraft$auraTick(CallbackInfo ci) {
        genshinecraft$auraState.tick((LivingEntity) (Object) this);
    }

    //? if >=26.3 {
    /*@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveAuras(ValueOutput output, CallbackInfo ci) {
        // Special statuses can outlive their triggering elemental aura.
        boolean hasStatuses = false;
        for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct", "dew", "dew_cooldown"}) {
            if (genshinecraft$auraState.statusTicks(key) > 0) {
                hasStatuses = true;
            }
        }
        if (!genshinecraft$auraState.snapshot().isEmpty()) {
        ValueOutput list = output.child(GENSHINCRAFT_AURAS);
        for (var entry : genshinecraft$auraState.snapshot().entrySet()) {
            ValueOutput aura = list.child(entry.getKey().name());
            aura.putInt("gauge_milli", Math.round(entry.getValue().gauge() * 1000f));
            aura.putInt("ticks", entry.getValue().remainingTicks());
            aura.putString("source_mode", entry.getValue().source().name());
            if (entry.getValue().applier() != null) {
                aura.putString("applier", entry.getValue().applier().toString());
            }
        }
        }
        if (hasStatuses) {
            ValueOutput flags = output.child(GENSHINCRAFT_REACTIONS);
            for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct", "dew", "dew_cooldown"}) {
                flags.putInt(key, genshinecraft$auraState.statusTicks(key));
            }
            flags.putInt("freeze_hits", genshinecraft$auraState.remainingFrozenHits());
            var electro = genshinecraft$auraState.electroChargedSource();
            if (genshinecraft$auraState.statusTicks("electro") > 0) {
                flags.putInt("electro_mastery", electro.mastery());
                if (electro.owner() != null) flags.putString("electro_owner", electro.owner().toString());
            }
            var burning = genshinecraft$auraState.burningSource();
            if (genshinecraft$auraState.statusTicks("burning") > 0) {
                flags.putInt("burning_mastery", burning.mastery());
                if (burning.owner() != null) flags.putString("burning_owner", burning.owner().toString());
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadAuras(ValueInput input, CallbackInfo ci) {
        genshinecraft$auraState.clear();
        input.child(GENSHINCRAFT_REACTIONS).ifPresent(flags -> {
            genshinecraft$auraState.restoreStatuses(
                    flags.getInt("electro").orElse(0),
                    flags.getInt("burning").orElse(0),
                    flags.getInt("quicken").orElse(0),
                    flags.getInt("frozen").orElse(0),
                    flags.getInt("superconduct").orElse(0));
            genshinecraft$auraState.restoreFrozenHits(flags.getInt("freeze_hits").orElse(0));
            genshinecraft$auraState.restoreDew(
                    flags.getInt("dew").orElse(0), flags.getInt("dew_cooldown").orElse(0));
            genshinecraft$auraState.restoreOngoingSources(
                    flags.getStringOr("electro_owner", ""), flags.getInt("electro_mastery").orElse(0),
                    flags.getStringOr("burning_owner", ""), flags.getInt("burning_mastery").orElse(0));
        });
        input.child(GENSHINCRAFT_AURAS).ifPresent(list -> {
            for (CarrierData.Element element : CarrierData.Element.values()) {
                list.child(element.name()).ifPresent(aura -> genshinecraft$restore(
                        element, aura.getInt("gauge_milli").orElse(0),
                        aura.getInt("ticks").orElse(0),
                        aura.getStringOr("applier", ""),
                        aura.getStringOr("source_mode", "NORMAL")));
            }
        });
    }
    *///?} else {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveAuras(CompoundTag tag, CallbackInfo ci) {
        // Special statuses can outlive their triggering elemental aura.
        boolean hasStatuses = false;
        for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct", "dew", "dew_cooldown"}) {
            if (genshinecraft$auraState.statusTicks(key) > 0) {
                hasStatuses = true;
            }
        }
        if (!genshinecraft$auraState.snapshot().isEmpty()) {
        CompoundTag saved = new CompoundTag();
        for (var entry : genshinecraft$auraState.snapshot().entrySet()) {
            CompoundTag aura = new CompoundTag();
            aura.putInt("gauge_milli", Math.round(entry.getValue().gauge() * 1000f));
            aura.putInt("ticks", entry.getValue().remainingTicks());
            aura.putString("source_mode", entry.getValue().source().name());
            if (entry.getValue().applier() != null) {
                aura.putString("applier", entry.getValue().applier().toString());
            }
            saved.put(entry.getKey().name(), aura);
        }
        tag.put(GENSHINCRAFT_AURAS, saved);
        }
        if (hasStatuses) {
            CompoundTag flags = new CompoundTag();
            for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct", "dew", "dew_cooldown"}) {
                flags.putInt(key, genshinecraft$auraState.statusTicks(key));
            }
            flags.putInt("freeze_hits", genshinecraft$auraState.remainingFrozenHits());
            var electro = genshinecraft$auraState.electroChargedSource();
            if (genshinecraft$auraState.statusTicks("electro") > 0) {
                flags.putInt("electro_mastery", electro.mastery());
                if (electro.owner() != null) flags.putString("electro_owner", electro.owner().toString());
            }
            var burning = genshinecraft$auraState.burningSource();
            if (genshinecraft$auraState.statusTicks("burning") > 0) {
                flags.putInt("burning_mastery", burning.mastery());
                if (burning.owner() != null) flags.putString("burning_owner", burning.owner().toString());
            }
            tag.put(GENSHINCRAFT_REACTIONS, flags);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadAuras(CompoundTag tag, CallbackInfo ci) {
        genshinecraft$auraState.clear();
        if (tag.contains(GENSHINCRAFT_REACTIONS)) {
            CompoundTag flags = tag.getCompound(GENSHINCRAFT_REACTIONS);
            genshinecraft$auraState.restoreStatuses(
                    flags.getInt("electro"), flags.getInt("burning"),
                    flags.getInt("quicken"), flags.getInt("frozen"),
                    flags.getInt("superconduct"));
            genshinecraft$auraState.restoreFrozenHits(
                    flags.getInt("freeze_hits"));
            genshinecraft$auraState.restoreDew(
                    flags.getInt("dew"), flags.getInt("dew_cooldown"));
            genshinecraft$auraState.restoreOngoingSources(
                    flags.getString("electro_owner"), flags.getInt("electro_mastery"),
                    flags.getString("burning_owner"), flags.getInt("burning_mastery"));
        }
        if (!tag.contains(GENSHINCRAFT_AURAS)) {
            return;
        }
        CompoundTag saved = tag.getCompound(GENSHINCRAFT_AURAS);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if (!saved.contains(element.name())) {
                continue;
            }
            CompoundTag aura = saved.getCompound(element.name());
            genshinecraft$restore(element, aura.getInt("gauge_milli"),
                    aura.getInt("ticks"), aura.getString("applier"), aura.getString("source_mode"));
        }
    }
    //?}

    @Unique
    private void genshinecraft$restore(CarrierData.Element element, int milli, int ticks,
                                      String applier, String sourceMode) {
        UUID uuid = null;
        if (!applier.isEmpty()) {
            try {
                uuid = UUID.fromString(applier);
            } catch (IllegalArgumentException ignored) {
                // A malformed source UUID must not prevent loading the entity.
            }
        }
        ElementalReactionEngine.Mode mode;
        try {
            mode = ElementalReactionEngine.Mode.valueOf(sourceMode);
        } catch (IllegalArgumentException exception) {
            mode = ElementalReactionEngine.Mode.NORMAL;
        }
        genshinecraft$auraState.restore(element, milli / 1000.0f, ticks, uuid, mode);
    }
}
