package dev.genshinecraft.element;

import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
//? if >=26.3 {
/*import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
*///?} else {
import net.minecraft.world.entity.MobSpawnType;
//?}
//? if =1.20.1 {
/*import net.minecraft.nbt.CompoundTag;
*///?}

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
//? if >=26.3 {
/*import net.minecraft.world.entity.monster.cubemob.Slime;
*///?} else {
import net.minecraft.world.entity.monster.Slime;
//?}

/** Seven registered, independently spawned Slime types with permanent inherent element. */
public final class ElementSlime extends Slime {
    private final CarrierData.Element element;

    public ElementSlime(EntityType<? extends Slime> type, Level level, CarrierData.Element element) {
        super(type, level);
        this.element = element;
    }

    public CarrierData.Element element() {
        return element;
    }

    /** Evaluate the actual spawn site: a biome alone is insufficient for water, lava, or rock. */
    public static boolean canSpawn(CarrierData.Element element, LevelAccessor level, BlockPos pos) {
        var biome = level.getBiome(pos);
        if (!biome.is(BiomeTags.IS_OVERWORLD)) return false;
        return switch (element) {
            case DENDRO -> biome.is(Biomes.PLAINS) || biome.is(Biomes.SUNFLOWER_PLAINS);
            case CRYO -> biome.value().getBaseTemperature() <= 0.15f;
            case PYRO -> nearFluid(level, pos, FluidTags.LAVA);
            case HYDRO -> nearFluid(level, pos, FluidTags.WATER);
            case ANEMO -> pos.getY() >= 120;
            case GEO -> rockyGround(level, pos);
            case ELECTRO -> biome.is(Biomes.SWAMP) || biome.is(Biomes.MANGROVE_SWAMP);
        };
    }

    private static boolean nearFluid(LevelAccessor level, BlockPos pos, TagKey<Fluid> fluid) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -2; y <= 2; y++) {
                    if (level.getFluidState(pos.offset(x, y, z)).is(fluid)) return true;
                }
            }
        }
        return false;
    }

    private static boolean rockyGround(LevelAccessor level, BlockPos pos) {
        int stone = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos ground = pos.offset(x, -1, z);
                if (level.getBlockState(ground).is(BlockTags.BASE_STONE_OVERWORLD)
                        || level.getBlockState(ground.below()).is(BlockTags.BASE_STONE_OVERWORLD)) {
                    if (++stone >= 8) return true;
                }
            }
        }
        return false;
    }

    // The natural spawn pack shares SpawnGroupData. With 3 members, the third
    // is guaranteed to be medium (size 2); packs of 1 or 2 remain small.
    //? if >=26.3 {
    /*@Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, SpawnGroupData group) {
        SpawnGroupData result = super.finalizeSpawn(world, difficulty, reason, group);
        if ((reason == EntitySpawnReason.NATURAL || reason == EntitySpawnReason.CHUNK_GENERATION)
                && result instanceof AgeableMob.AgeableMobGroupData data) {
            setSize(data.getGroupSize() == 3 ? 2 : 1, true);
        }
        return result;
    }
    *///?} else if =1.20.1 {
    /*@Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType reason, SpawnGroupData group, CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(world, difficulty, reason, group, tag);
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return result;
        SlimePackData pack = result instanceof SlimePackData data ? data : new SlimePackData();
        setSize(++pack.count == 3 ? 2 : 1, true);
        return pack;
    }
    *///?} else {
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType reason, SpawnGroupData group) {
        SpawnGroupData result = super.finalizeSpawn(world, difficulty, reason, group);
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return result;
        SlimePackData pack = result instanceof SlimePackData data ? data : new SlimePackData();
        setSize(++pack.count == 3 ? 2 : 1, true);
        return pack;
    }
    //?}
    //? if <26.3 {
    private static final class SlimePackData implements SpawnGroupData {
        private int count;
    }
    //?}

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel) {
            var aura = ((ElementalAuraHolder) (Object) this).genshinecraft$auras();
            if (!aura.has(element)) {
                aura.apply(element, null);
            }
        }
    }

    //? if >=26.3 {
    /*@Override
    public boolean hurtServer(ServerLevel server, DamageSource source, float amount) {
        return ElementalCombat.sourceElement(source) != element && super.hurtServer(server, source, amount);
    }
    *///?} else {
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return ElementalCombat.sourceElement(source) != element && super.hurt(source, amount);
    }
    //?}

    // A level-one elemental slime must still be able to deal elemental contact damage.
    //? if >=26.3 {
    /*@Override
    protected boolean canDealDamage() { return true; }
    *///?} else {
    @Override
    protected boolean isDealsDamage() { return true; }
    //?}

    @Override
    protected void dealDamage(LivingEntity target) {
        if (!(level() instanceof ServerLevel server) || !isAlive() || !target.isAlive()
                || distanceToSqr(target) >= Math.pow(0.6 * getSize(), 2)
                || !hasLineOfSight(target)) return;
        DamageSource source = ElementalCombat.mobSource(server, element, this);
        //? if >=26.3 {
        /*boolean dealt = target.hurtServer(server, source, Math.max(1f, getAttackDamage()));
        *///?} else {
        boolean dealt = target.hurt(source, Math.max(1f, getAttackDamage()));
        //?}
        if (dealt) {
            ((ElementalAuraHolder) target).genshinecraft$auras().apply(element, getUUID());
        }
    }
}
