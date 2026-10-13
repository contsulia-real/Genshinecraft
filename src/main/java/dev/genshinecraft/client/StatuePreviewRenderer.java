package dev.genshinecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
//? if >=26.3 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} else {
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
//?}

/** Uses actual Minecraft baked block geometry in a rotated 3D view, never pixel-map colors. */
public final class StatuePreviewRenderer {
    private StatuePreviewRenderer() {}

    //? if >=26.3 {
    /*public static void draw(GuiGraphicsExtractor graphics, StatuePreviewScene.Snapshot scene,
                            int x, int y, int width, int height, float yaw) {
        StatuePreviewPip.submit(graphics, scene, x, y, width, height, yaw);
    }
    *///?} else {
    public static void draw(GuiGraphics graphics, StatuePreviewScene.Snapshot scene,
                            int x, int y, int width, int height, float yaw) {
        if (scene == null || scene.parts().isEmpty()) return;
        graphics.flush();
        graphics.enableScissor(x, y, x + width, y + height);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + width * 0.50f, y + height * 0.59f, 210.0f);
        pose.scale(6.7f, -6.7f, 6.7f);
        pose.mulPose(Axis.XP.rotationDegrees(24.0f));
        pose.mulPose(Axis.YP.rotationDegrees(yaw));
        pose.translate(-0.5f, -5.5f, -4.5f);

        var client = Minecraft.getInstance();
        var buffers = client.renderBuffers().bufferSource();
        for (StatuePreviewScene.Part block : scene.parts()) {
            pose.pushPose();
            pose.translate(block.x(), block.y(), block.z());
            client.getBlockRenderer().renderSingleBlock(block.state(), pose, buffers,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
        buffers.endBatch();
        pose.popPose();
        graphics.disableScissor();
    }
    //?}
}
