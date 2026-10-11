package dev.genshinecraft.mixin;

import dev.genshinecraft.Genshinecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=26.3 {
/*import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
//?}

/** Swap the actual entity model texture for an icy material while frozen. */
@Mixin(LivingEntityRenderer.class)
public abstract class FrozenEntityTextureMixin {
    //? if >=26.3 {
    /*private static final Identifier ICE =
            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "textures/entity/frozen_ice.png");

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$iceMaterial(LivingEntityRenderState state,
                                            boolean visible, boolean translucent,
                                            boolean glowing, CallbackInfoReturnable<RenderType> cir) {
        if (state.isFullyFrozen) {
            cir.setReturnValue(RenderTypes.entityTranslucent(ICE));
        }
    }
    *///?} else {
    //? if =1.20.1 {
    /*private static final ResourceLocation ICE =
            new ResourceLocation(Genshinecraft.MOD_ID, "textures/entity/frozen_ice.png");
    *///?} else {
    private static final ResourceLocation ICE =
            ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, "textures/entity/frozen_ice.png");
    //?}

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$iceMaterial(LivingEntity entity, boolean visible,
                                            boolean translucent, boolean glowing,
                                            CallbackInfoReturnable<RenderType> cir) {
        if (entity.isFullyFrozen()) {
            cir.setReturnValue(RenderType.entityTranslucent(ICE));
        }
    }
    //?}
}
