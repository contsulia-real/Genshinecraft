package dev.genshinecraft.world;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/** No inventory slots. The two GUI buttons use vanilla container-button packets. */
public final class StatueOfferingMenu extends AbstractContainerMenu {
    private static final int SYNC_SIZE = 16;
    private final ServerLevel serverLevel;
    private final BlockPos altar;
    private final CarrierData.Element altarElement;
    private final InteractionHand hand;
    private final ContainerData synced;

    /** Vanilla client menu factory; server data arrives through menu data slots. */
    public StatueOfferingMenu(int id, Inventory inventory) {
        super(StatueMenus.TYPE.get(), id);
        this.serverLevel = null;
        this.altar = BlockPos.ZERO;
        this.altarElement = null;
        this.hand = InteractionHand.MAIN_HAND;
        this.synced = new SimpleContainerData(SYNC_SIZE);
        addDataSlots(synced);
    }

    /** Only invoked on the authoritative server after source-carrier validation. */
    public StatueOfferingMenu(int id, Inventory inventory, ServerLevel level, BlockPos pos,
                              CarrierData.Element element, InteractionHand hand) {
        super(StatueMenus.TYPE.get(), id);
        this.serverLevel = level;
        this.altar = pos.immutable();
        this.altarElement = element;
        this.hand = hand;
        this.synced = new ContainerData() {
            @Override
            public int get(int index) {
                var progress = StatueOfferings.get(level).progress(element == null
                        ? StatueOfferings.moonId(inventory.player) : StatueOfferings.id(level, altar));
                return switch (index) {
                    case 0 -> low(altar.getX());
                    case 1 -> high(altar.getX());
                    case 2 -> low(altar.getZ());
                    case 3 -> high(altar.getZ());
                    case 4 -> altar.getY();
                    case 5 -> element == null ? 8 : element.ordinal() + 1;
                    case 6 -> low(progress.level());
                    case 7 -> high(progress.level());
                    case 8 -> low(progress.contributed());
                    case 9 -> part(progress.contributed(), 16);
                    case 10 -> part(progress.contributed(), 32);
                    case 11 -> part(progress.contributed(), 48);
                    case 12 -> low(StatueOfferings.countSigils(inventory.player, element));
                    case 13 -> high(StatueOfferings.countSigils(inventory.player, element));
                    case 14 -> low(element == null
                            ? StatueOfferings.moonLimit(StatueOfferings.get(level).worldLevel())
                            : Integer.MAX_VALUE);
                    case 15 -> high(element == null
                            ? StatueOfferings.moonLimit(StatueOfferings.get(level).worldLevel())
                            : Integer.MAX_VALUE);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Server is authoritative; received values only update client-side SimpleContainerData.
            }

            @Override
            public int getCount() { return SYNC_SIZE; }
        };
        addDataSlots(synced);
    }

    public static Component title() {
        return Component.translatable("screen.genshinecraft.offering.title");
    }

    private static int low(long value) { return (int) value & 0xffff; }
    private static int high(long value) { return (int) (value >>> 16) & 0xffff; }
    private static int part(long value, int shift) { return (int) (value >>> shift) & 0xffff; }
    private int intValue(int lo) { return (synced.get(lo) & 0xffff) | (synced.get(lo + 1) << 16); }

    public BlockPos statuePosition() {
        return new BlockPos(intValue(0), (short) synced.get(4), intValue(2));
    }

    public CarrierData.Element element() {
        int id = synced.get(5) - 1;
        var elements = CarrierData.Element.values();
        return id >= 0 && id < elements.length ? elements[id] : null;
    }

    public boolean moon() { return synced.get(5) == 8; }

    public int level() { return Math.max(1, intValue(6)); }

    public long contributed() {
        return ((long) synced.get(8) & 0xffffL) | (((long) synced.get(9) & 0xffffL) << 16)
                | (((long) synced.get(10) & 0xffffL) << 32)
                | (((long) synced.get(11) & 0xffffL) << 48);
    }

    public int availableSigils() { return Math.max(0, intValue(12)); }

    public int offeringLimit() { return intValue(14); }

    public long remainingToNextLevel() {
        return Math.max(0L, StatueOfferings.cost((long) level() + 1) - contributed());
    }

    @Override
    public boolean stillValid(Player player) {
        if (serverLevel == null) return true;
        return player.level() == serverLevel
                && player.blockPosition().distSqr(altar) <= 64
                && (altarElement == null
                    ? serverLevel.getBlockState(altar).getBlock() == StatueBlocks.MOON_ALTAR.get()
                    : serverLevel.getBlockState(altar).getBlock() == StatueBlocks.ALTARS.get(altarElement).get()
                        && StatueOfferings.authentic(player, altar, altarElement, hand));
    }

    /** Button 0: one full level only. Button 1: all matching sigils, including multiple levels. */
    @Override
    public boolean clickMenuButton(Player player, int button) {
        if (serverLevel == null || !stillValid(player) || (button != 0 && button != 1)) return false;
        int consumed = StatueOfferings.get(serverLevel).donate(
                serverLevel, altar, altarElement, player, hand, button == 1);
        if (consumed > 0) broadcastChanges();
        return consumed > 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
