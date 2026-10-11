package dev.genshinecraft.combat;

import dev.genshinecraft.item.CarrierData;
import java.util.UUID;

/** Server-side elemental snapshot of a projectile's firing weapon. */
public interface ElementalProjectile {
    UUID genshinecraft$carrierId();
    CarrierData.Element genshinecraft$element();
}
