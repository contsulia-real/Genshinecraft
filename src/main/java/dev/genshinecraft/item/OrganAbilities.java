package dev.genshinecraft.item;

import dev.genshinecraft.element.ElementalAuraHolder;
import dev.genshinecraft.element.ElementalReactionEffects;
import dev.genshinecraft.world.ElementOres;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

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
        // One-block reach from the player's body, not their eyes: the old eye-distance
        // check rejected a block directly beside the player's feet.
        if (!player.getBoundingBox().inflate(1.0).intersects(new AABB(pos))) {
            return InteractionResult.PASS;
        }
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
            case DENDRO -> BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), server, pos);
            case ANEMO -> accelerate(server, pos);
            case GEO -> mineOre(server, pos, player);
            default -> false;
        };
        if (!changed) return InteractionResult.PASS;
        cooldown(player, ctx.getItemInHand());
        return InteractionResult.SUCCESS;
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

    private static boolean accelerate(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
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
        if (element != CarrierData.Element.CRYO && element != CarrierData.Element.ELECTRO) return InteractionResult.PASS;
        if (!target.isAlive()
                || !player.getBoundingBox().inflate(1.0).intersects(target.getBoundingBox())) {
            return InteractionResult.PASS;
        }
        //? if >=26.3 {
        /*if (player.level().isClientSide()) return InteractionResult.SUCCESS;
        *///?} else {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        //?}
        if (!(player.level() instanceof ServerLevel server) || !ready(player, organ)) {
            return InteractionResult.PASS;
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
        ((ElementalAuraHolder) target).genshinecraft$auras().apply(element, player.getUUID());
        cooldown(player, organ);
        return InteractionResult.SUCCESS;
    }

    /** Hits only the entity the player faces within one block, not every nearby entity. */
    public static InteractionResult useInAir(Level level, Player player, ItemStack organ,
                                             CarrierData.Element element) {
        if (element != CarrierData.Element.CRYO && element != CarrierData.Element.ELECTRO) return InteractionResult.PASS;
        Vec3 origin = player.getEyePosition();
        Vec3 end = origin.add(player.getLookAngle());
        LivingEntity nearest = null;
        double distance = Double.MAX_VALUE;
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(1.0), e -> e != player && e.isAlive())) {
            var hit = living.getBoundingBox().inflate(0.15).clip(origin, end);
            if (hit.isEmpty()) continue;
            double d = origin.distanceToSqr(hit.get());
            if (d < distance && d <= 1.0) {
                nearest = living;
                distance = d;
            }
        }
        return nearest == null ? InteractionResult.PASS : useOnEntity(player, organ, nearest, element);
    }
}
