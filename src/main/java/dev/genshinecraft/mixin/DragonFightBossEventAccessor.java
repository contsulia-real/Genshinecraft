package dev.genshinecraft.mixin;

import net.minecraft.server.level.ServerBossEvent;
//? if >=26.3 {
/*import net.minecraft.world.level.dimension.end.EnderDragonFight;
*///?} else {
import net.minecraft.world.level.dimension.end.EndDragonFight;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The vanilla dragon boss bar belongs to the End fight, not the dragon entity. */
//? if >=26.3 {
/*@Mixin(EnderDragonFight.class)
*///?} else {
@Mixin(EndDragonFight.class)
//?}
public interface DragonFightBossEventAccessor {
    @Accessor("dragonEvent")
    ServerBossEvent genshinecraft$dragonBar();
}
