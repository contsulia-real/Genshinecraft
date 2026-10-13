package dev.genshinecraft.client;

import dev.genshinecraft.element.AuraIconHolder;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.mixin.BossOverlayEventsAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
//? if >=26.3 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Client-only placement of the current, vanilla-synced elemental aura icons. */
public final class AuraIconOverlay {
    private static final char FIRST_GLYPH = '\ue100';

    private AuraIconOverlay() {}

    /** Ordinary entities: place the icon column beside the projected world-space entity. */
    //? if >=26.3 {
    /*public static void render(GuiGraphicsExtractor graphics) {
    *///?} else {
    public static void render(GuiGraphics graphics) {
    //?}
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;
        renderPlayerAuras(graphics, client);
        List<LivingEntity> affected = affectedEntities(client);
        if (affected.isEmpty()) return;

        // Boss bars have their own later rendering pass; never draw their auras in-world.
        Set<UUID> onBossBars = matchedBossIds(client, affected);
        //? if >=26.3 {
        /*Camera camera = client.gameRenderer.mainCamera();
        double fov = camera.getFov();
        Vector3fc look = camera.forwardVector();
        Vector3fc upAxis = camera.upVector();
        Vector3fc leftAxis = camera.leftVector();
        Vec3 cameraPosition = camera.position();
        *///?} else {
        Camera camera = client.gameRenderer.getMainCamera();
        double fov = client.options.fov().get();
        Vector3fc look = camera.getLookVector();
        Vector3fc upAxis = camera.getUpVector();
        Vector3fc leftAxis = camera.getLeftVector();
        Vec3 cameraPosition = camera.getPosition();
        //?}

        // The game's camera basis is authoritative. In particular, right = -camera.left;
        // the previous hand-derived +X "right" mirrored mobs to the wrong side of screen.
        Vec3 forward = new Vec3(look.x(), look.y(), look.z());
        Vec3 right = new Vec3(-leftAxis.x(), -leftAxis.y(), -leftAxis.z());
        Vec3 up = new Vec3(upAxis.x(), upAxis.y(), upAxis.z());
        double focal = graphics.guiHeight() * 0.5 /
                Math.tan(Math.toRadians(Math.max(30, Math.min(110, fov))) * 0.5);

        for (LivingEntity living : affected) {
            if (living == client.player || client.player.distanceToSqr(living) > 25.0
                    || onBossBars.contains(living.getUUID())
                    || living.isInvisibleTo(client.player)) continue;
            Vec3 target = new Vec3(living.getX(), living.getY() + living.getBbHeight() * 0.72,
                    living.getZ());
            // The HUD has no depth buffer: avoid drawing elemental icons through terrain.
            if (client.level.clip(new ClipContext(cameraPosition, target,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, client.player))
                    .getType() == HitResult.Type.BLOCK) continue;
            Vec3 offset = target.subtract(cameraPosition);
            double depth = offset.dot(forward);
            if (depth < 0.3 || depth > 128) continue;
            int mask = ((AuraIconHolder) living).genshinecraft$auraIcons();
            int count = Integer.bitCount(mask);
            double projectionX = graphics.guiWidth() * 0.5 + offset.dot(right) * focal / depth;
            double projectionY = graphics.guiHeight() * 0.5 - offset.dot(up) * focal / depth;
            int x = (int) Math.round(projectionX
                    + living.getBbWidth() * focal / (2.0 * depth) + 5);
            int y = (int) Math.round(projectionY - count * 7);
            if (x < 0 || x > graphics.guiWidth() - 10 || y < -10
                    || y + count * 14 > graphics.guiHeight() + 10) continue;
            for (CarrierData.Element element : CarrierData.Element.values()) {
                if ((mask & (1 << element.ordinal())) == 0) continue;
                glyph(graphics, client, x, y, element.ordinal(), false);
                y += 14;
            }
        }
    }

    /** Local player's aura is a horizontal HUD row, never a world-space label. */
    //? if >=26.3 {
    /*private static void renderPlayerAuras(GuiGraphicsExtractor graphics, Minecraft client) {
    *///?} else {
    private static void renderPlayerAuras(GuiGraphics graphics, Minecraft client) {
    //?}
        int mask = ((AuraIconHolder) client.player).genshinecraft$auraIcons();
        if (mask == 0) return;
        int count = Integer.bitCount(mask);
        boolean creative = client.player.getAbilities().instabuild;
        int x;
        int y;
        if (creative) {
            // Vanilla creative HUD has no armor row: center icons above the hotbar.
            x = graphics.guiWidth() / 2 - count * 11 / 2;
            y = graphics.guiHeight() - 39;
        } else {
            // Vanilla positions armor above the hearts, moving it up for extra heart rows.
            int rows = Math.max(1, (int) Math.ceil((Math.max(client.player.getMaxHealth(),
                    client.player.getHealth()) + Math.ceil(client.player.getAbsorptionAmount())) / 20.0));
            int rowHeight = Math.max(10 - (rows - 2), 3);
            int armorY = graphics.guiHeight() - 49 - (rows - 1) * rowHeight;
            x = graphics.guiWidth() / 2 - 91 + (80 - count * 11) / 2;
            y = armorY - 12;
        }
        for (CarrierData.Element element : CarrierData.Element.values()) {
            if ((mask & (1 << element.ordinal())) == 0) continue;
            glyph(graphics, client, x, y, element.ordinal(), true);
            x += 11;
        }
    }

    /** Called immediately AFTER vanilla draws its boss bars, in the same render stratum. */
    //? if >=26.3 {
    /*public static void renderBosses(GuiGraphicsExtractor graphics) {
    *///?} else {
    public static void renderBosses(GuiGraphics graphics) {
    //?}
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;
        List<LivingEntity> affected = affectedEntities(client);
        if (affected.isEmpty()) return;

        Set<UUID> assigned = new HashSet<>();
        int barY = 12;
        for (LerpingBossEvent event : bossOverlay(client).genshinecraft$events().values()) {
            if (barY >= graphics.guiHeight() / 3) break;
            LivingEntity owner = findMatchingBoss(event, affected, assigned, client);
            if (owner != null) {
                assigned.add(owner.getUUID());
                int mask = ((AuraIconHolder) owner).genshinecraft$auraIcons();
                int x = graphics.guiWidth() / 2 - 91 + 4;
                int iconIndex = 0;
                for (CarrierData.Element element : CarrierData.Element.values()) {
                    if ((mask & (1 << element.ordinal())) == 0) continue;
                    glyph(graphics, client, x + 10 * iconIndex++, barY + 8,
                            element.ordinal(), true);
                }
            }
            barY += 19; // Vanilla spacing: 10 + font.lineHeight (9 px).
        }
    }

    private static List<LivingEntity> affectedEntities(Minecraft client) {
        List<LivingEntity> result = new ArrayList<>();
        // Iterate all CLIENT-TRACKED entities, not just a 64-block sphere around the
        // player. A visible boss bar may belong to a mob outside that small sphere.
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity living && living.isAlive()
                    && ((AuraIconHolder) living).genshinecraft$auraIcons() != 0) {
                result.add(living);
            }
        }
        return result;
    }

    private static BossOverlayEventsAccessor bossOverlay(Minecraft client) {
        //? if >=26.3 {
        /*BossHealthOverlay overlay = client.gui.hud.getBossOverlay();
        *///?} else {
        BossHealthOverlay overlay = client.gui.getBossOverlay();
        //?}
        return (BossOverlayEventsAccessor) overlay;
    }

    private static Set<UUID> matchedBossIds(Minecraft client, List<LivingEntity> entities) {
        Set<UUID> assigned = new HashSet<>();
        for (LerpingBossEvent event : bossOverlay(client).genshinecraft$events().values()) {
            LivingEntity boss = findMatchingBoss(event, entities, assigned, client);
            if (boss != null) assigned.add(boss.getUUID());
        }
        return assigned;
    }

    private static LivingEntity findMatchingBoss(LerpingBossEvent bar,
            List<LivingEntity> entities, Set<UUID> alreadyAssigned, Minecraft client) {
        // The server synchronizes the owner's actual BossEvent UUID. Translated
        // names and entity UUIDs are NOT reliable substitutes for the event ID.
        String bossBarId = bar.getId().toString();
        for (LivingEntity living : entities) {
            if (!alreadyAssigned.contains(living.getUUID())
                    && bossBarId.equals(((AuraIconHolder) living).genshinecraft$bossBarId())) {
                return living;
            }
        }
        return null;
    }

    //? if >=26.3 {
    /*private static void glyph(GuiGraphicsExtractor graphics, Minecraft client,
                              int x, int y, int index, boolean small) {
    *///?} else {
    private static void glyph(GuiGraphics graphics, Minecraft client,
                              int x, int y, int index, boolean small) {
    //?}
        String family = small ? "attached_elements_bar" : "attached_elements";
        //? if >=26.3 {
        /*Component value = Component.literal(String.valueOf((char) (FIRST_GLYPH + index)))
                .withStyle(style -> style.withColor(0xffffff)
                        .withFont(new FontDescription.Resource(
                                Identifier.fromNamespaceAndPath("genshinecraft", family))));
        graphics.text(client.font, value, x, y, -1, false);
        *///?} else if =1.21.1 {
        Component value = Component.literal(String.valueOf((char) (FIRST_GLYPH + index)))
                .withStyle(style -> style.withColor(0xffffff)
                        .withFont(ResourceLocation.fromNamespaceAndPath("genshinecraft", family)));
        graphics.drawString(client.font, value, x, y, -1, false);
        //?} else {
        /*Component value = Component.literal(String.valueOf((char) (FIRST_GLYPH + index)))
                .withStyle(style -> style.withColor(0xffffff)
                        .withFont(new ResourceLocation("genshinecraft", family)));
        graphics.drawString(client.font, value, x, y, -1, false);
        *///?}
    }
}
