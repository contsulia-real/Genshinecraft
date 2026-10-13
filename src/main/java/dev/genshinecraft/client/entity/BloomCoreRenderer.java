package dev.genshinecraft.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.genshinecraft.element.ElementalBloomCore;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import org.joml.Matrix4f;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
//?}

public final class BloomCoreRenderer extends
        //? if >=26.3 {
        /*EntityRenderer<ElementalBloomCore, BloomCoreRenderer.State> {
        *///?} else {
        EntityRenderer<ElementalBloomCore> {
        //?}
    //? if >=26.3 {
    /*private final ItemModelResolver items;
    *///?} else {
    private final ItemRenderer items;
    //?}

    public BloomCoreRenderer(EntityRendererProvider.Context context) {
        super(context);
        items = context.
                //? if >=26.3 {
                /*getItemModelResolver();
                *///?} else {
                getItemRenderer();
                //?}
        shadowRadius = 0.15f;
    }

    //? if >=26.3 {
    /*public static final class State extends EntityRenderState {
        final ItemStackRenderState model = new ItemStackRenderState();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ElementalBloomCore entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        items.updateForNonLiving(state.model, new ItemStack(CarrierItems.DENDRO_CORE_MODEL.get()),
                ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void submit(State state, PoseStack stack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        stack.pushPose();
        transform(stack, state.ageInTicks);
        state.model.submit(stack, collector, 0xF000F0, OverlayTexture.NO_OVERLAY, state.outlineColor);
        stack.popPose();
        super.submit(state, stack, collector, camera);
    }
    *///?} else {
    @Override
    public ResourceLocation getTextureLocation(ElementalBloomCore entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public void render(ElementalBloomCore entity, float yaw, float partialTick,
                       PoseStack stack, MultiBufferSource buffers, int light) {
        stack.pushPose();
        transform(stack, entity.tickCount + partialTick);
        items.renderStatic(new ItemStack(CarrierItems.DENDRO_CORE_MODEL.get()),
                ItemDisplayContext.GROUND, 0xF000F0, OverlayTexture.NO_OVERLAY,
                stack, buffers, entity.level(), entity.getId());
        stack.popPose();
        super.render(entity, yaw, partialTick, stack, buffers, light);
    }
    //?}

    private static void transform(PoseStack pose, float age) {
        pose.translate(0, 0.28 + Math.sin(age * 0.12) * 0.035, 0);
        //? if >=26.3 {
        /*pose.mulPose(new Matrix4f().rotationY((float) Math.toRadians(age * 1.4f)));
        *///?} else {
        pose.mulPose(Axis.YP.rotationDegrees(age * 1.4f));
        //?}
    }
}
