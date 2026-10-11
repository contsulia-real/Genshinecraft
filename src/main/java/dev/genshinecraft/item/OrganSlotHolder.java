package dev.genshinecraft.item;

import net.minecraft.world.SimpleContainer;

/** The single extra slot belongs to the player, not to a temporary screen. */
public interface OrganSlotHolder {
    SimpleContainer genshinecraft$organSlot();
}
