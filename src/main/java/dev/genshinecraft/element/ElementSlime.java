package dev.genshinecraft.element;

import dev.genshinecraft.combat.ElementalCombat;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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
