package dev.genshinecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Client-side settings opened from the loader's Mods / Configure menu. */
public final class GenshinecraftConfigScreen extends Screen {
    private final Screen parent;

    public GenshinecraftConfigScreen(Screen parent) {
        super(Component.translatable("config.genshinecraft.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = (this.width - 220) / 2;
        int middle = this.height / 2;
        this.addRenderableWidget(Button.builder(optionLabel(), button -> {
            DamageNumberSettings.setEnabled(!DamageNumberSettings.enabled());
            button.setMessage(optionLabel());
        }).bounds(left, middle - 10, 220, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"),
                button -> onClose()).bounds(left, middle + 24, 220, 20).build());
    }

    private static Component optionLabel() {
        return Component.translatable("config.genshinecraft.damage_numbers").append(": ")
                .append(Component.translatable(DamageNumberSettings.enabled()
                        ? "options.on" : "options.off"));
    }

    //? if >=26.3 {
    /*@Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor graphics,
                                   int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 54, 0xFFFFFF);
    }
    *///?} else {
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics graphics,
                       int mouseX, int mouseY, float delta) {
        //? if =1.20.1 {
        /*renderBackground(graphics);
        *///?} else {
        renderBackground(graphics, mouseX, mouseY, delta);
        //?}
        super.render(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2,
                this.height / 2 - 54, 0xFFFFFF);
    }
    //?}

    @Override
    public void onClose() {
        //? if >=26.3 {
        /*Minecraft.getInstance().gui.setScreen(parent);
        *///?} else {
        Minecraft.getInstance().setScreen(parent);
        //?}
    }
}
