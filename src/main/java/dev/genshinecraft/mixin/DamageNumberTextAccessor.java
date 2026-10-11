package dev.genshinecraft.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Display.TextDisplay.class)
public interface DamageNumberTextAccessor {
    @Invoker("setText")
    void genshinecraft$text(Component text);

    @Invoker("setBackgroundColor")
    void genshinecraft$background(int color);
}
