package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
//? if >=26.3 {
/*import net.minecraft.world.damagesource.DamageSource;
*///?}
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
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
import java.util.UUID;

/** Registered elemental shard entity; never a collectible ItemStack. */
public final class ElementalCrystals extends Entity {
    private static final int MAX_SHARDS = 3;
    private static final int LIFE_TICKS = 300;
    private static final String ELEMENT_KEY = "genshinecraft.element";
    private static final String MOON_KEY = "genshinecraft.moon";
    private static final String ORBIT_KEY = "genshinecraft.orbit";
    private static final String TARGET_KEY = "genshinecraft.target";
    private UUID moonTarget;
    private static final EntityDataAccessor<Boolean> MOON =
            SynchedEntityData.defineId(ElementalCrystals.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ORBIT =
            SynchedEntityData.defineId(ElementalCrystals.class, EntityDataSerializers.INT);

    public boolean moonCage() { return getEntityData().get(MOON); }
    private static final EntityDataAccessor<Integer> ELEMENT =
            SynchedEntityData.defineId(ElementalCrystals.class, EntityDataSerializers.INT);

    public Element element() {
        int ordinal = getEntityData().get(ELEMENT);
        return ordinal >= 0 && ordinal < Element.values().length
                ? Element.values()[ordinal] : Element.PYRO;
    }

    public ElementalCrystals(EntityType<? extends ElementalCrystals> type, Level level) {
        super(type, level);
    }

    /** Three individual visible moon cages based on the Hydro crystal model. */
    public static void createMoonCages(ServerLevel level, LivingEntity target) {
        boolean found = level.getEntitiesOfClass(ElementalCrystals.class,
                target.getBoundingBox().inflate(4), e -> e.moonCage()
                        && target.getUUID().equals(e.moonTarget)).size() >= 3;
        if (found) return;
        for (int index = 0; index < 3; index++) {
            ElementalCrystals cage = new ElementalCrystals(ElementalEntities.CRYSTAL_SHARD.get(), level);
            cage.getEntityData().set(ELEMENT, Element.HYDRO.ordinal());
            cage.getEntityData().set(MOON, true);
            cage.getEntityData().set(ORBIT, index);
            cage.moonTarget = target.getUUID();
            cage.setNoGravity(true);
            cage.setSilent(true);
            cage.setPos(target.getX(), target.getY() + target.getBbHeight() * 0.65, target.getZ());
            level.addFreshEntity(cage);
        }
    }

    public static void releaseMoonCages(ServerLevel level, LivingEntity target) {
        for (ElementalCrystals cage : level.getEntitiesOfClass(ElementalCrystals.class,
                target.getBoundingBox().inflate(5), e -> e.moonCage()
                        && target.getUUID().equals(e.moonTarget))) {
            level.sendParticles(dev.genshinecraft.particle.ElementalParticles.reaction("lunar_crystallize"),
                    cage.getX(), cage.getY(), cage.getZ(), 8, 0.25, 0.25, 0.25, 0.05);
            cage.discard();
        }
    }

    public static void drop(ServerLevel level, LivingEntity target, Element element) {
        if (element != Element.PYRO && element != Element.HYDRO
                && element != Element.ELECTRO && element != Element.CRYO) {
            return;
        }
        List<ElementalCrystals> existing = new ArrayList<>();
        for (var entity : level.getAllEntities()) {
            if (entity instanceof ElementalCrystals shard && !shard.moonCage()) {
                existing.add(shard);
            }
        }
        if (existing.size() >= MAX_SHARDS) {
            existing.stream().max(Comparator.comparingInt(e -> e.tickCount))
                    .ifPresent(ElementalCrystals::discard);
        }

        ElementalCrystals shard = new ElementalCrystals(ElementalEntities.CRYSTAL_SHARD.get(), level);
        shard.getEntityData().set(ELEMENT, element.ordinal());
        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        shard.setPos(target.getX() + Math.cos(angle), target.getY() + 0.25,
                target.getZ() + Math.sin(angle));
        shard.setNoGravity(true);
        shard.setSilent(true);
        shard.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(shard);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        setDeltaMovement(Vec3.ZERO);
        if (moonCage()) {
            LivingEntity target = moonTarget == null ? null
                    : level.getEntity(moonTarget) instanceof LivingEntity living ? living : null;
            if (target == null || !target.isAlive() || tickCount >= 160) {
                discard();
                return;
            }
            double angle = tickCount * 0.075 + getEntityData().get(ORBIT) * (Math.PI * 2 / 3);
            setPos(target.getX() + Math.cos(angle) * 1.1,
                    target.getY() + target.getBbHeight() * 0.66 + Math.sin(tickCount * 0.09) * 0.12,
                    target.getZ() + Math.sin(angle) * 1.1);
            return; // Moon cages are orbiting reaction objects, never absorption pickups.
        }
        if (tickCount >= LIFE_TICKS) {
            discard();
            return;
        }
        if (tickCount < 10) {
            return;
        }
        for (Player player : level.getEntitiesOfClass(Player.class,
                getBoundingBox().inflate(0.65), p -> p.isAlive() && !p.isSpectator())) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0, false, true));
            discard();
            break;
        }
    }

    //? if >=26.3 {
    /*@Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }
    *///?}

    private void restoreTarget(String saved) {
        moonTarget = null;
        if (!saved.isBlank()) try {
            moonTarget = UUID.fromString(saved);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void restoreElement(String saved) {
        try {
            getEntityData().set(ELEMENT, Element.valueOf(saved).ordinal());
        } catch (IllegalArgumentException exception) {
            getEntityData().set(ELEMENT, Element.PYRO.ordinal());
        }
    }

    //? if =1.20.1 {
    /*@Override
    protected void defineSynchedData() {
        getEntityData().define(ELEMENT, Element.PYRO.ordinal());
        getEntityData().define(MOON, false);
        getEntityData().define(ORBIT, 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    *///?} else {
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ELEMENT, Element.PYRO.ordinal());
        builder.define(MOON, false);
        builder.define(ORBIT, 0);
    }
    //?}

    //? if >=26.3 {
    /*@Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putString(ELEMENT_KEY, element().name());
        output.putBoolean(MOON_KEY, moonCage());
        output.putInt(ORBIT_KEY, getEntityData().get(ORBIT));
        if (moonTarget != null) output.putString(TARGET_KEY, moonTarget.toString());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        restoreElement(input.getStringOr(ELEMENT_KEY, "PYRO"));
        getEntityData().set(MOON, input.getBooleanOr(MOON_KEY, false));
        getEntityData().set(ORBIT, input.getIntOr(ORBIT_KEY, 0));
        restoreTarget(input.getStringOr(TARGET_KEY, ""));
    }
    *///?} else {
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString(ELEMENT_KEY, element().name());
        tag.putBoolean(MOON_KEY, moonCage());
        tag.putInt(ORBIT_KEY, getEntityData().get(ORBIT));
        if (moonTarget != null) tag.putString(TARGET_KEY, moonTarget.toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        restoreElement(tag.getString(ELEMENT_KEY));
        getEntityData().set(MOON, tag.getBoolean(MOON_KEY));
        getEntityData().set(ORBIT, tag.getInt(ORBIT_KEY));
        restoreTarget(tag.getString(TARGET_KEY));
    }
    //?}
}
