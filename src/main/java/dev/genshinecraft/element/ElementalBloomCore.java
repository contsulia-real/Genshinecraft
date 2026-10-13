package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.combat.DamageNumbers;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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

/** Registered Dendro Core entity. Its visual is a sprouting seed, not vanilla text. */
public final class ElementalBloomCore extends Entity {
    private static final int MAX_CORES = 5;
    private static final int LIFE_TICKS = 120;
    private static final String OWNER_KEY = "genshinecraft.owner";
    private static final String LUNAR_KEY = "genshinecraft.lunar";
    private static final String MASTERY_KEY = "genshinecraft.lunar_mastery";
    private UUID owner;
    private boolean lunar;
    private int reactionMastery;

    public ElementalBloomCore(EntityType<? extends ElementalBloomCore> type, Level level) {
        super(type, level);
    }

    public static void spawn(ServerLevel level, LivingEntity target, Player owner, boolean lunar, int mastery) {
        spawn(level, target, owner.getUUID(), lunar, mastery);
    }

    public static void spawn(ServerLevel level, LivingEntity target, UUID owner, boolean lunar, int mastery) {
        List<ElementalBloomCore> existing = new ArrayList<>();
        for (var entity : level.getAllEntities()) {
            if (entity instanceof ElementalBloomCore core && !core.isRemoved()
                    && (owner == null
                        ? core.owner == null && core.distanceToSqr(target) <= 64.0
                        : owner.equals(core.owner))) {
                existing.add(core);
            }
        }
        if (existing.size() >= MAX_CORES) {
            existing.stream().max(Comparator.comparingInt(core -> core.tickCount))
                    .ifPresent(core -> detonate(level, core, 2f, 2.5, "bloom", core.owner, core.reactionMastery));
        }
        ElementalBloomCore core = new ElementalBloomCore(ElementalEntities.DENDRO_CORE.get(), level);
        core.owner = owner;
        core.lunar = lunar;
        core.reactionMastery = Math.max(0, mastery);
        double angle = level.getRandom().nextDouble() * Math.PI * 2;
        core.setPos(target.getX() + Math.cos(angle), target.getY() + 0.22,
                target.getZ() + Math.sin(angle));
        core.setNoGravity(true);
        core.setSilent(true);
        level.addFreshEntity(core);
        particles(level, core.getX(), core.getY(), core.getZ(), 5, "bloom");
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel server)) {
            return;
        }
        if (tickCount % 12 == 0) {
            particles(server, getX(), getY(), getZ(), 2, "bloom");
        }
        if (tickCount >= LIFE_TICKS) {
            detonate(server, this, 2f, 2.5, "bloom", owner, reactionMastery);
        }
    }

    /** Make the core a real melee/projectile target, rather than an untargetable visual. */
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    //? if >=26.3 {
    /*@Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return attacked(level, source, amount);
    }
    *///?} else {
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return level() instanceof ServerLevel server && attacked(server, source, amount);
    }
    //?}

    private boolean attacked(ServerLevel level, DamageSource source, float amount) {
        if (isRemoved() || amount <= 0f || !(source.getEntity() instanceof Player attacker)) {
            return false;
        }
        Element incoming = ElementalCombat.sourceElement(source);
        int mastery = ElementalCombat.reactionMastery(attacker, source);
        if (incoming == Element.PYRO || incoming == Element.ELECTRO) {
            triggerCore(level, this, null, incoming, attacker, mastery);
        } else {
            detonate(level, this, 2f, 2.5, "bloom", owner, reactionMastery);
        }
        return true;
    }

    /** Indirect fire/electro hits still activate one nearby core. */
    public static boolean trigger(ServerLevel level, LivingEntity hit, Element incoming,
                                  Player attacker, int mastery) {
        if (incoming != Element.PYRO && incoming != Element.ELECTRO) return false;
        List<ElementalBloomCore> cores = level.getEntitiesOfClass(ElementalBloomCore.class,
                hit.getBoundingBox().inflate(2.8));
        ElementalBloomCore nearest = cores.stream()
                .filter(core -> !core.isRemoved())
                .min(Comparator.comparingDouble((ElementalBloomCore core) -> core.distanceToSqr(hit))
                        .thenComparingInt(Entity::getId))
                .orElse(null);
        return nearest != null && triggerCore(level, nearest, hit, incoming, attacker, mastery);
    }

    /** Shared resolution for a directly attacked core or an indirectly triggered core. */
    private static boolean triggerCore(ServerLevel level, ElementalBloomCore core, LivingEntity hit,
                                       Element incoming, Player attacker, int mastery) {
        if (core.isRemoved()) return false;
        if (incoming == Element.PYRO) {
            detonate(level, core, 4f, 3.0, "burgeon", attacker.getUUID(), mastery);
        } else {
            // A core is single-use even if its damage synchronously triggers other effects.
            core.discard();
            LivingEntity selected = hit != null && canDamage(core, hit, attacker.getUUID(), attacker)
                    ? hit : null;
            double nearest = selected == null ? Double.MAX_VALUE : selected.distanceToSqr(core);
            for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
                    core.getBoundingBox().inflate(5.0),
                    candidate -> canDamage(core, candidate, attacker.getUUID(), attacker))) {
                double distance = other.distanceToSqr(core);
                if (distance < nearest) {
                    nearest = distance;
                    selected = other;
                }
            }
            if (selected != null) {
                ElementalReactionEffects.dealDamage(level, selected, Element.DENDRO, attacker.getUUID(),
                        ElementalReactionEffects.transformativeDamage(4f, mastery),
                        core.lunar ? DamageNumbers.Gradient.LUNAR_BLOOM : null);
                if (selected.isAlive()) applyLunarDew(level, core, List.of(selected));
            }
            level.sendParticles(ElementalParticles.reaction("hyperbloom"),
                    selected == null ? core.getX() : selected.getX(),
                    selected == null ? core.getY() : selected.getY() + selected.getBbHeight() * 0.6,
                    selected == null ? core.getZ() : selected.getZ(),
                    12, 0.25, 0.3, 0.25, 0.08);
        }
        return true;
    }

    /** A reaction never hits either owner or triggerer; other players obey vanilla PvP rules. */
    private static boolean canDamage(ElementalBloomCore core, LivingEntity target,
                                     UUID triggerOwner, Player triggerer) {
        if (!target.isAlive() || target.getUUID().equals(core.owner)
                || target.getUUID().equals(triggerOwner)) return false;
        return !(target instanceof Player player)
                || (triggerer != null && triggerer.canHarmPlayer(player));
    }

    private static void detonate(ServerLevel level, ElementalBloomCore core,
                                 float damage, double radius, String effect,
                                 UUID triggerOwner, int mastery) {
        if (core.isRemoved()) return;
        core.discard();
        float scaledDamage = ElementalReactionEffects.transformativeDamage(damage, mastery);
        Player triggerer = triggerOwner == null ? null : level.getPlayerByUUID(triggerOwner);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                core.getBoundingBox().inflate(radius),
                target -> canDamage(core, target, triggerOwner, triggerer));
        for (LivingEntity target : targets) {
            ElementalReactionEffects.dealDamage(level, target, Element.DENDRO, triggerOwner,
                    scaledDamage, core.lunar ? DamageNumbers.Gradient.LUNAR_BLOOM : null);
        }
        targets.removeIf(target -> !target.isAlive());
        applyLunarDew(level, core, targets);
        particles(level, core.getX(), core.getY(), core.getZ(), 14, effect);
    }

    /** Spend one owner's charge for one lunar core, even when the blast hits several enemies. */
    private static void applyLunarDew(ServerLevel level, ElementalBloomCore core,
                                      List<LivingEntity> targets) {
        if (!core.lunar || core.owner == null || targets.isEmpty()) return;
        ServerPlayer player = level.getServer().getPlayerList().getPlayer(core.owner);
        if (player == null) return;
        ElementalAuraState state = ((ElementalAuraHolder) player).genshinecraft$auras();
        if (!state.spendVerdantDew()) return;
        ElementalReactionEffects.notifyVerdantDew(player, state.verdantDew());
        for (LivingEntity target : targets) {
            ElementalReactionEffects.dealSpecialDamage(level, target, Element.DENDRO,
                    core.owner, 2f, core.reactionMastery, DamageNumbers.Gradient.LUNAR_BLOOM);
            ElementalReactionEffects.spawnReaction(level, target, "lunar_dew", 8);
        }
    }

    private static void particles(ServerLevel level, double x, double y, double z,
                                  int count, String effect) {
        level.sendParticles(ElementalParticles.reaction(effect),
                x, y, z, count, 0.25, 0.2, 0.25, 0.04);
    }

    private void restoreOwner(String saved) {
        owner = null;
        if (!saved.isEmpty()) {
            try {
                owner = UUID.fromString(saved);
            } catch (IllegalArgumentException ignored) {
                // A corrupt owner ID must not prevent a world from loading.
            }
        }
    }

    //? if =1.20.1 {
    /*@Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    *///?} else {
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }
    //?}

    //? if >=26.3 {
    /*@Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (owner != null) {
            output.putString(OWNER_KEY, owner.toString());
        }
        output.putBoolean(LUNAR_KEY, lunar);
        output.putInt(MASTERY_KEY, reactionMastery);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        restoreOwner(input.getStringOr(OWNER_KEY, ""));
        lunar = input.getBooleanOr(LUNAR_KEY, false);
        reactionMastery = Math.max(0, input.getIntOr(MASTERY_KEY, 0));
    }
    *///?} else {
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) {
            tag.putString(OWNER_KEY, owner.toString());
        }
        tag.putBoolean(LUNAR_KEY, lunar);
        tag.putInt(MASTERY_KEY, reactionMastery);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        restoreOwner(tag.getString(OWNER_KEY));
        lunar = tag.getBoolean(LUNAR_KEY);
        reactionMastery = Math.max(0, tag.getInt(MASTERY_KEY));
    }
    //?}
}
