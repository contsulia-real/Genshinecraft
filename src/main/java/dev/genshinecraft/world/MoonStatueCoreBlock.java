package dev.genshinecraft.world;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
//? if =1.20.1 {
/*import net.minecraft.world.InteractionHand;
*///?} else {
import net.minecraft.world.InteractionHand;
//?}
//? if =1.21.1 {
import net.minecraft.world.ItemInteractionResult;
//?}

/** Independent shrine: no elemental resonance, no organ ownership prerequisite. */
public final class MoonStatueCoreBlock extends Block {
    public MoonStatueCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos,
                        BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (level instanceof ServerLevel server && !oldState.is(state.getBlock())) {
            StatueOfferings.get(server).recordMoon(server, pos);
        }
    }

    //? if =1.20.1 {
    /*@Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        return offer(level, pos, player, hand);
    }
    *///?} else {
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        return offer(level, pos, player, InteractionHand.MAIN_HAND);
    }

    //? if =1.21.1 {
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               InteractionHand hand, BlockHitResult hit) {
        offer(level, pos, player, hand);
        return ItemInteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        return offer(level, pos, player, hand);
    }
    *///?}
    //?}

    private InteractionResult offer(Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer) {
            var provider = new SimpleMenuProvider(
                    (id, inventory, ignored) ->
                            new StatueOfferingMenu(id, inventory, server, pos, null, hand),
                    Component.translatable("screen.genshinecraft.offering.moon_title"));
            //? if >=26.3 {
            /*dev.architectury.registry.menu.MenuRegistry.openExtendedMenu(serverPlayer, provider, buffer -> {});
            *///?} else {
            serverPlayer.openMenu(provider);
            //?}
        }
        return InteractionResult.SUCCESS;
    }
}
