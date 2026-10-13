package dev.genshinecraft.item;

import dev.genshinecraft.element.ElementalAuraHolder;
import dev.genshinecraft.element.ElementalReactionEffects;
import dev.genshinecraft.element.ElementalReactionEngine;
import dev.genshinecraft.world.ElementOres;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Active right-click powers of a valid owned external magic organ. One block, twenty ticks. */
public final class OrganAbilities {
    private OrganAbilities() {}

    private static boolean ready(Player player, ItemStack organ) {
        CarrierData data = CarrierData.read(organ);
        if (data == null || !data.usableBy(player.getUUID())) return false;
        //? if >=26.3 {
        /*return !player.getCooldowns().isOnCooldown(organ);
        *///?} else {
        return !player.getCooldowns().isOnCooldown(organ.getItem());
        //?}
    }

    private static void cooldown(Player player, ItemStack organ) {
        //? if >=26.3 {
        /*player.getCooldowns().addCooldown(organ, 20);
        *///?} else {
        player.getCooldowns().addCooldown(organ.getItem(), 20);
        //?}
    }

    public static InteractionResult useOn(UseOnContext ctx, CarrierData.Element element) {
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        boolean supported = switch (element) {
            case PYRO, HYDRO, DENDRO, ANEMO, GEO -> true;
            default -> false;
        };
        if (!supported) return InteractionResult.PASS;
        //? if >=26.3 {
        /*if (level.isClientSide()) return InteractionResult.SUCCESS;
        *///?} else {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        //?}
        if (!(level instanceof ServerLevel server)
                || !ready(player, ctx.getItemInHand())
                || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
            return InteractionResult.PASS;
        }
        boolean changed = switch (element) {
            case PYRO -> ignite(server, pos.relative(ctx.getClickedFace()), player);
            case HYDRO -> water(server, pos, ctx.getClickedFace(), player);
            case DENDRO -> matureCrop(server, pos);
            case ANEMO -> accelerate(server, pos);
            case GEO -> mineOre(server, pos, player);
            default -> false;
        };
        if (!changed) return InteractionResult.PASS;
        cooldown(player, ctx.getItemInHand());
        return InteractionResult.SUCCESS;
    }

    /** Age-based crops mature immediately; bonemealable crops also get repeated vanilla growth. */
    private static boolean matureCrop(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(Blocks.SUGAR_CANE) || state.is(Blocks.CACTUS) || state.is(Blocks.BAMBOO)) {
            int maxHeight = state.is(Blocks.BAMBOO) ? 8 : 3;
            BlockPos base = pos;
            while (level.getBlockState(base.below()).is(state.getBlock())) base = base.below();
            int height = 0;
            while (height < maxHeight && level.getBlockState(base.above(height)).is(state.getBlock())) {
                height++;
            }
            boolean grown = false;
            while (height < maxHeight) {
                BlockPos next = base.above(height);
                if (!level.isEmptyBlock(next)
                        || !state.getBlock().defaultBlockState().canSurvive(level, next)) break;
                //? if >=26.3 {
                /*level.setBlock(next, state.getBlock().defaultBlockState(), 3, 512);
                *///?} else {
                level.setBlock(next, state.getBlock().defaultBlockState(), 3);
                //?}
                grown = true;
                height++;
            }
            return grown;
        }
        for (var property : state.getProperties()) {
            if (property instanceof IntegerProperty age && age.getName().equals("age")) {
                int max = age.getPossibleValues().stream().mapToInt(Integer::intValue).max().orElse(0);
                if (state.getValue(age) < max) {
                    //? if >=26.3 {
                    /*return level.setBlock(pos, state.setValue(age, max), 3, 512);
                    *///?} else {
                    return level.setBlock(pos, state.setValue(age, max), 3);
                    //?}
                }
                break;
            }
        }
        if (state.getBlock() instanceof BonemealableBlock crop) {
            boolean grown = false;
            for (int i = 0; i < 32; i++) {
                state = level.getBlockState(pos);
                //? if =1.20.1 {
                /*if (!(state.getBlock() instanceof BonemealableBlock current)
                        || !current.isValidBonemealTarget(level, pos, state, false)) break;
                *///?} else if >=26.3 {
                /*if (!(state.getBlock() instanceof BonemealableBlock current)
                        || !current.isValidBonemealTarget(level, pos, state,
                                net.minecraft.world.level.block.BonemealSource.INTERACTION)) break;
                *///?} else {
                if (!(state.getBlock() instanceof BonemealableBlock current)
                        || !current.isValidBonemealTarget(level, pos, state)) break;
                //?}
                //? if >=26.3 {
                /*current.performBonemeal(level, level.getRandom(), pos, state,
                        net.minecraft.world.level.block.BonemealSource.INTERACTION);
                *///?} else {
                current.performBonemeal(level, level.getRandom(), pos, state);
                //?}
                grown = true;
            }
            return grown;
        }
        return false;
    }

    private static boolean ignite(ServerLevel level, BlockPos pos, Player player) {
        if (!level.mayInteract(player, pos) || !level.getBlockState(pos).canBeReplaced()
                || !Blocks.FIRE.defaultBlockState().canSurvive(level, pos)) return false;
        //? if >=26.3 {
        /*return level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3, 512);
        *///?} else {
        return level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        //?}
    }

    private static boolean water(ServerLevel level, BlockPos clicked, Direction face, Player player) {
        BlockPos pos = level.getBlockState(clicked).canBeReplaced() ? clicked : clicked.relative(face);
        if (!level.mayInteract(player, pos) || !level.getBlockState(pos).canBeReplaced()) return false;
        //? if >=26.3 {
        /*return level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3, 512);
        *///?} else {
        return level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
        //?}
    }

    /** Accelerate both vanilla random ticks and block-entity tickers (furnaces, brewers, etc). */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean accelerate(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.getBlock() instanceof EntityBlock entityBlock) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                BlockEntityTicker ticker = entityBlock.getTicker(level, state, blockEntity.getType());
                if (ticker != null) {
                    for (int i = 0; i < 20; i++) {
                        var current = level.getBlockState(pos);
                        if (blockEntity.isRemoved() || current.getBlock() != state.getBlock()) break;
                        ticker.tick(level, pos, current, blockEntity);
                    }
                    return true;
                }
            }
        }
        if (!state.isRandomlyTicking()) return false;
        for (int i = 0; i < 20; i++) {
            var current = level.getBlockState(pos);
            if (!current.isRandomlyTicking()) break;
            current.randomTick(level, pos, level.getRandom());
        }
        return true;
    }

    private static boolean mineOre(ServerLevel level, BlockPos pos, Player player) {
        var state = level.getBlockState(pos);
        boolean ore = false;
        boolean geoOre = false;
        for (var element : CarrierData.Element.values()) {
            if (state.is(ElementOres.ORES.get(element).get())
                    || state.is(ElementOres.DEEPSLATE_ORES.get(element).get())) {
                ore = true;
                geoOre = element == CarrierData.Element.GEO;
                break;
            }
        }
        if (!ore || !player.mayUseItemAt(pos, Direction.UP, player.getMainHandItem())) return false;
        //? if >=26.3 {
        /*var drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos));
        *///?} else {
        var drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player,
                new ItemStack(Items.NETHERITE_PICKAXE));
        //?}
        if (!level.destroyBlock(pos, false, player)) return false;
        for (ItemStack drop : drops) {
            // Replace the 5% bonus on non-Geo ores rather than stacking another random roll.
            if (!geoOre && drop.is(CarrierItems.SIGILS.get(CarrierData.Element.GEO).get())) continue;
            Block.popResource(level, pos, drop);
        }
        // The right-click mining power always grants one additional Geo sigil,
        // independently of the ore's normal loot (including ordinary bonus chance).
        Block.popResource(level, pos, new ItemStack(CarrierItems.SIGILS.get(CarrierData.Element.GEO).get()));
        return true;
    }

    public static InteractionResult useOnEntity(Player player, ItemStack organ, LivingEntity target,
                                                CarrierData.Element element) {
        if (element != CarrierData.Element.CRYO && element != CarrierData.Element.ELECTRO
                && element != CarrierData.Element.DENDRO && element != CarrierData.Element.PYRO)
            return InteractionResult.PASS;
        if (!target.isAlive()) return InteractionResult.PASS;
        //? if >=26.3 {
        /*if (player.level().isClientSide()) return InteractionResult.SUCCESS;
        *///?} else {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        //?}
        if (!(player.level() instanceof ServerLevel server) || !ready(player, organ)) {
            return InteractionResult.PASS;
        }
        if (element == CarrierData.Element.DENDRO) {
            if (!(target instanceof AgeableMob baby) || baby.getAge() >= 0) return InteractionResult.PASS;
            baby.setAge(0);
            cooldown(player, organ);
            return InteractionResult.SUCCESS;
        }
        // All offensive organ interactions use the same elemental-reaction entry point as
        // melee/projectile attacks. Applying a bare aura bypasses Overloaded and friends.
        ElementalReactionEngine.Mode mode = ElementalReactionEngine.Mode.NORMAL;
        for (CarrierData.Element type : CarrierData.Element.values()) {
            if (organ.is(CarrierItems.MOON_WHEELS.get(type).get())) {
                mode = ElementalReactionEngine.Mode.LUNAR;
                break;
            }
            if (organ.is(CarrierItems.STAR_WEDGES.get(type).get())) {
                mode = ElementalReactionEngine.Mode.STELLAR;
                break;
            }
        }
        ElementalReactionEffects.onHit(server, player, target, element, "organ_right_click", mode,
                CarrierData.read(organ).elementalMastery());
        if (element == CarrierData.Element.PYRO) {
            //? if =1.20.1 {
            /*target.setSecondsOnFire(4);
            *///?} else {
            target.igniteForSeconds(4);
            //?}
        }
        if (element == CarrierData.Element.CRYO) {
            //? if >=26.3 {
            /*target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 0), player);
            *///?} else {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0), player);
            //?}
        } else {
            ElementalReactionEffects.dealDamage(server, target, element, player.getUUID(), 2.0f);
        }
        cooldown(player, organ);
        return InteractionResult.SUCCESS;
    }

}
