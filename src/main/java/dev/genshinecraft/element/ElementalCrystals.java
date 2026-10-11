package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
//? if >=26.3 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?} else {
import net.minecraft.nbt.CompoundTag;
//?}

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Registered elemental shard entity; never a collectible ItemStack. */
public final class ElementalCrystals extends Display.TextDisplay {
    private static final int MAX_SHARDS = 3;
    private static final int LIFE_TICKS = 300;
    private static final String ELEMENT_KEY = "genshinecraft.element";
    private Element element = Element.PYRO;

    public ElementalCrystals(EntityType<? extends ElementalCrystals> type, Level level) {
        super(type, level);
    }

    public static void drop(ServerLevel level, LivingEntity target, Element element) {
        if (element != Element.PYRO && element != Element.HYDRO
                && element != Element.ELECTRO && element != Element.CRYO) {
            return;
        }
        List<ElementalCrystals> existing = new ArrayList<>();
        for (var entity : level.getAllEntities()) {
            if (entity instanceof ElementalCrystals shard) {
                existing.add(shard);
            }
        }
        if (existing.size() >= MAX_SHARDS) {
            existing.stream().max(Comparator.comparingInt(e -> e.tickCount))
                    .ifPresent(ElementalCrystals::discard);
        }

        ElementalCrystals shard = new ElementalCrystals(ElementalEntities.CRYSTAL_SHARD.get(), level);
        shard.element = element;
        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        shard.setPos(target.getX() + Math.cos(angle), target.getY() + 0.25,
                target.getZ() + Math.sin(angle));
        shard.setNoGravity(true);
        shard.setSilent(true);
        shard.setDeltaMovement(Vec3.ZERO);
        shard.updateAppearance();
        level.addFreshEntity(shard);
    }

    private void updateAppearance() {
        char symbol = switch (element) {
            case PYRO -> '\uE001';
            case HYDRO -> '\uE002';
            case ELECTRO -> '\uE003';
            case CRYO -> '\uE004';
            default -> '\uE001';
        };
        ElementalDisplaySprites.apply(this, symbol);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        setDeltaMovement(Vec3.ZERO);
        if (tickCount >= LIFE_TICKS) {
            discard();
            return;
        }
        if (tickCount < 10) {
            return;
        }
        for (Player player : level.getEntitiesOfClass(Player.class,
                getBoundingBox().inflate(0.65), p -> p.isAlive() && !p.isSpectator())) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 0, false, true));
            discard();
            break;
        }
    }

    private void restoreElement(String saved) {
        try {
            element = Element.valueOf(saved);
        } catch (IllegalArgumentException exception) {
            element = Element.PYRO;
        }
        updateAppearance();
    }

    //? if >=26.3 {
    /*@Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString(ELEMENT_KEY, element.name());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        restoreElement(input.getStringOr(ELEMENT_KEY, "PYRO"));
    }
    *///?} else {
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(ELEMENT_KEY, element.name());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        restoreElement(tag.getString(ELEMENT_KEY));
    }
    //?}
}
