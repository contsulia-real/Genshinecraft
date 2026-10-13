package dev.genshinecraft.client;

//? if >=26.3 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.StainedGlassBlock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Native picture-in-picture depth-buffered 3D rendering of block models and atlas textures.
public final class StatuePreviewPip {
    private static Field guiRenderStateField;
    private static boolean installed;

    private StatuePreviewPip() {}

    public record State(List<StatuePreviewScene.Part> parts, float yaw,
                        int x0, int y0, int x1, int y1, float scale,
                        ScreenRectangle scissorArea) implements PictureInPictureRenderState {
        @Override
        public ScreenRectangle bounds() {
            return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
        }
    }

    private static final class Renderer extends PictureInPictureRenderer<State> {
        @Override
        public Class<State> getRenderStateClass() {
            return State.class;
        }

        @Override
        protected String getTextureLabel() {
            return "Genshinecraft statue 3D camera";
        }

        @Override
        protected void renderToTexture(State preview, PoseStack pose, SubmitNodeCollector out) {
            var client = Minecraft.getInstance();
            var bakedStates = client.getModelManager().getBlockStateModelSet();
            pose.pushPose();
            pose.rotateDegrees(Axis.XP, 24);
            pose.rotateDegrees(Axis.YP, preview.yaw());
            pose.translate(-0.5f, -5.5f, -4.5f);
            var rand = RandomSource.create(5029);
            List<BlockStateModelPart> parts = new ArrayList<>();
            int[] color = {-1, -1, -1, -1};
            for (var part : preview.parts()) {
                parts.clear();
                rand.setSeed(42L);
                bakedStates.get(part.state()).collectParts(rand, parts);
                if (parts.isEmpty()) continue;
                pose.pushPose();
                pose.translate(part.x(), part.y(), part.z());
                out.submitBlockModel(pose,
                        part.state().getBlock() instanceof StainedGlassBlock
                                ? RenderTypes.translucentMovingBlock()
                                : RenderTypes.solidMovingBlock(),
                        List.copyOf(parts), color, 15728880, 0, 0);
                pose.popPose();
            }
            pose.popPose();
        }
    }

    // Register alongside vanilla PiP renderers on the already-constructed GameRenderer.
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void install() {
        if (installed) return;
        try {
            var gameRenderer = Minecraft.getInstance().gameRenderer;
            Field guiField = gameRenderer.getClass().getDeclaredField("guiRenderer");
            guiField.setAccessible(true);
            GuiRenderer renderer = (GuiRenderer) guiField.get(gameRenderer);
            Field mapField = GuiRenderer.class.getDeclaredField("pictureInPictureRenderers");
            mapField.setAccessible(true);
            Map<Class<?>, PictureInPictureRenderer<?>> existing =
                    (Map<Class<?>, PictureInPictureRenderer<?>>) mapField.get(renderer);
            if (!existing.containsKey(State.class)) {
                Map<Class<?>, PictureInPictureRenderer<?>> extended = new HashMap<>(existing);
                extended.put(State.class, new Renderer());
                mapField.set(renderer, extended);
            }
            guiRenderStateField = GuiGraphicsExtractor.class.getDeclaredField("guiRenderState");
            guiRenderStateField.setAccessible(true);
            installed = true;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot register the native 3D statue preview renderer", exception);
        }
    }

    public static void submit(GuiGraphicsExtractor graphics, StatuePreviewScene.Snapshot scene,
                              int x, int y, int width, int height, float yaw) {
        if (scene == null || scene.parts().isEmpty()) return;
        install();
        try {
            GuiRenderState state = (GuiRenderState) guiRenderStateField.get(graphics);
            state.addPicturesInPictureState(new State(scene.parts(), yaw, x, y, x + width, y + height,
                    6.7f, null));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot submit native statue preview", exception);
        }
    }
}
*///?} else {
public final class StatuePreviewPip {
    private StatuePreviewPip() {}
}
//?}
