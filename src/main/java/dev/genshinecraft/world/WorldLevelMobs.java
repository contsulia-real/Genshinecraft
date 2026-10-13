package dev.genshinecraft.world;

import dev.genshinecraft.Genshinecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else if =1.21.1 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import java.util.UUID;
*///?}

/** World Level scaling for friendly, neutral and hostile mobs; never players or bosses. */
public final class WorldLevelMobs {
    //? if >=26.3 {
    /*private static final Identifier HEALTH_ID =
            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "world_level_health");
    *///?} else if =1.21.1 {
    private static final ResourceLocation HEALTH_ID =
            ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, "world_level_health");
    //?} else {
    /*private static final UUID HEALTH_ID = UUID.fromString("4979403c-cdc6-4c3d-9b36-f9fd44bc0b94");
    *///?}

    private WorldLevelMobs() {}

    public static boolean eligible(Entity entity) {
        return entity instanceof Mob
                && !(entity instanceof WitherBoss)
                && !(entity instanceof EnderDragon);
    }

    public static void tick(LivingEntity entity) {
        if (!(entity instanceof Mob mob) || !eligible(mob)
                || !(mob.level() instanceof ServerLevel level) || mob.tickCount % 20 != 0) return;
        var attribute = mob.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) return;
        int worldLevel = StatueOfferings.get(level).worldLevel();
        double amount = worldLevel * 0.2;
        var previous = attribute.getModifier(HEALTH_ID);
        //? if =1.20.1 {
        /*if (previous != null && Math.abs(previous.getAmount() - amount) < 0.000001) return;
        *///?} else {
        if (previous != null && Math.abs(previous.amount() - amount) < 0.000001) return;
        //?}
        double before = mob.getMaxHealth();
        if (previous != null) attribute.removeModifier(HEALTH_ID);
        if (worldLevel > 0) {
            //? if =1.20.1 {
            /*attribute.addPermanentModifier(new AttributeModifier(HEALTH_ID, "Genshinecraft world health",
                    amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
            *///?} else {
            attribute.addPermanentModifier(new AttributeModifier(HEALTH_ID,
                    amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            //?}
        }
        if (before > 0 && mob.isAlive()) {
            mob.setHealth((float) Math.min(mob.getMaxHealth(),
                    mob.getHealth() * mob.getMaxHealth() / before));
        }
    }

    public static float scaleDamage(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (amount <= 0 || !eligible(attacker) || !(attacker.level() instanceof ServerLevel level)) {
            return amount;
        }
        return amount * (1.0f + StatueOfferings.get(level).worldLevel() * 0.1f);
    }
}
