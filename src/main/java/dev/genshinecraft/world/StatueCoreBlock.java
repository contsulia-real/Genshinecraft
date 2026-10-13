package dev.genshinecraft.world;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
//? if =1.20.1 {
/*import net.minecraft.world.InteractionHand;
*///?} else {
import net.minecraft.world.InteractionHand;
//?}
//? if =1.21.1 {
import net.minecraft.world.ItemInteractionResult;
//?}

public final class StatueCoreBlock extends Block {
    public static final BooleanProperty ATTEMPTED = BooleanProperty.create("attempted");
    private final CarrierData.Element element;

    public StatueCoreBlock(Properties properties, CarrierData.Element element) {
        super(properties);
        this.element = element;
        registerDefaultState(defaultBlockState().setValue(ATTEMPTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ATTEMPTED);
    }

    //? if =1.20.1 {
    /*@Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        return interact(state, level, pos, player, hand);
    }
    *///?} else {
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        return interact(state, level, pos, player, InteractionHand.MAIN_HAND);
    }

    //? if =1.21.1 {
    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                               BlockPos pos, Player player,
                                               InteractionHand hand, BlockHitResult hit) {
        interact(state, level, pos, player, hand);
        return ItemInteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    protected InteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                           BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        return interact(state, level, pos, player, hand);
    }
    *///?}
    //?}

    private InteractionResult interact(BlockState state, Level level, BlockPos pos,
                                       Player player, InteractionHand hand) {
        if (level instanceof ServerLevel server && player instanceof ServerPlayer serverPlayer
                && StatueOfferings.authentic(player, pos, element, hand)) {
            var provider = new SimpleMenuProvider(
                    (id, inventory, ignored) -> new StatueOfferingMenu(
                            id, inventory, server, pos, element, hand),
                    StatueOfferingMenu.title());
            //? if >=26.3 {
            /*dev.architectury.registry.menu.MenuRegistry.openExtendedMenu(serverPlayer, provider, buffer -> {});
            *///?} else {
            serverPlayer.openMenu(provider);
            //?}
            return InteractionResult.SUCCESS;
        }
        return resonate(state, level, pos, player);
    }

    private InteractionResult resonate(BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(ATTEMPTED)) {
            player.sendSystemMessage(Component.translatable("message.genshinecraft.statue.used"));
            return InteractionResult.SUCCESS;
        }

        if (hasFourValidCarriers(player)) {
            player.sendSystemMessage(Component.translatable("message.genshinecraft.statue.carrier_limit"));
            return InteractionResult.SUCCESS;
        }

        // A single server-side state transition makes every result permanent for this statue.
        //? if >=26.3 {
        /*boolean changed = level.setBlock(pos, state.setValue(ATTEMPTED, true), 3, 512);
        *///?} else {
        boolean changed = level.setBlock(pos, state.setValue(ATTEMPTED, true), 3);
        //?}
        if (!changed) {
            return InteractionResult.SUCCESS;
        }
        if (server.getRandom().nextFloat() >= 0.80f) {
            player.sendSystemMessage(Component.translatable("message.genshinecraft.statue.failed"));
            return InteractionResult.SUCCESS;
        }

        String origin = StatueOfferings.id(server, pos);
        ItemStack reward = CarrierData.createFromStatue(StatueRewardStyles.choose(server, pos, element), element, origin);
        CarrierData.claim(reward, player);
        Component rewardName = reward.getHoverName();
        if (!player.addItem(reward)) {
            server.addFreshEntity(new ItemEntity(server, player.getX(), player.getY() + 0.5, player.getZ(), reward));
        }
        player.sendSystemMessage(Component.translatable("message.genshinecraft.statue.success",
                rewardName));
        return InteractionResult.SUCCESS;
    }

    /** Per-player limit: four usable, owned external magic organs in the inventory. */
    private static boolean hasFourValidCarriers(Player player) {
        int count = 0;
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            CarrierData data = CarrierData.read(inventory.getItem(slot));
            if (data != null && data.usableBy(player.getUUID()) && ++count >= 4) {
                return true;
            }
        }
        return false;
    }
}
