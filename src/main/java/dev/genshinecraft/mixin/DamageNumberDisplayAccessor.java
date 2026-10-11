package dev.genshinecraft.mixin;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.class)
public interface DamageNumberDisplayAccessor {
    @Invoker("setBillboardConstraints")
    void genshinecraft$billboard(Display.BillboardConstraints alignment);

    @Invoker("setTransformation")
    void genshinecraft$transformation(Transformation transformation);
}
