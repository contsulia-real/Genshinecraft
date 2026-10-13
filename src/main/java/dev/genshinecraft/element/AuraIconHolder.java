package dev.genshinecraft.element;

/** Vanilla-synced elemental aura bitmask on each LivingEntity. */
public interface AuraIconHolder {
    int genshinecraft$auraIcons();
    void genshinecraft$setAuraIcons(int mask);
    String genshinecraft$bossBarId();
    void genshinecraft$setBossBarId(String id);
}
