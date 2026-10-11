package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalAuraHolder;
import dev.genshinecraft.element.ElementalAuraState;
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
public abstract class LivingElementalAuraMixin implements ElementalAuraHolder {
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
        for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct"}) {
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
            if (entry.getValue().applier() != null) {
                aura.putString("applier", entry.getValue().applier().toString());
            }
        }
        }
        if (hasStatuses) {
            ValueOutput flags = output.child(GENSHINCRAFT_REACTIONS);
            for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct"}) {
                flags.putInt(key, genshinecraft$auraState.statusTicks(key));
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$loadAuras(ValueInput input, CallbackInfo ci) {
        genshinecraft$auraState.clear();
        input.child(GENSHINCRAFT_REACTIONS).ifPresent(flags -> genshinecraft$auraState.restoreStatuses(
                flags.getInt("electro").orElse(0),
                flags.getInt("burning").orElse(0),
                flags.getInt("quicken").orElse(0),
                flags.getInt("frozen").orElse(0),
                flags.getInt("superconduct").orElse(0)));
        input.child(GENSHINCRAFT_AURAS).ifPresent(list -> {
            for (CarrierData.Element element : CarrierData.Element.values()) {
                list.child(element.name()).ifPresent(aura -> genshinecraft$restore(
                        element, aura.getInt("gauge_milli").orElse(0),
                        aura.getInt("ticks").orElse(0),
                        aura.getStringOr("applier", "")));
            }
        });
    }
    *///?} else {
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void genshinecraft$saveAuras(CompoundTag tag, CallbackInfo ci) {
        // Special statuses can outlive their triggering elemental aura.
        boolean hasStatuses = false;
        for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct"}) {
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
            if (entry.getValue().applier() != null) {
                aura.putString("applier", entry.getValue().applier().toString());
            }
            saved.put(entry.getKey().name(), aura);
        }
        tag.put(GENSHINCRAFT_AURAS, saved);
        }
        if (hasStatuses) {
            CompoundTag flags = new CompoundTag();
            for (String key : new String[]{"electro", "burning", "quicken", "frozen", "superconduct"}) {
                flags.putInt(key, genshinecraft$auraState.statusTicks(key));
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
                    aura.getInt("ticks"), aura.getString("applier"));
        }
    }
    //?}

    @Unique
    private void genshinecraft$restore(CarrierData.Element element, int milli, int ticks, String applier) {
        UUID uuid = null;
        if (!applier.isEmpty()) {
            try {
                uuid = UUID.fromString(applier);
            } catch (IllegalArgumentException ignored) {
                // A malformed source UUID must not prevent loading the entity.
            }
        }
        genshinecraft$auraState.restore(element, milli / 1000.0f, ticks, uuid);
    }
}
