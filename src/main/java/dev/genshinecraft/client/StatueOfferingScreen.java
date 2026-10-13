package dev.genshinecraft.client;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.world.StatueOfferingMenu;
import dev.genshinecraft.world.StatueOfferings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//? if >=26.3 {
/*import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
//?}

/**
 * Native-looking villager trading screen with a true 3D block-model preview.
 * Left: the 3D world structure fills the panel. Right: villager-style level trades
 * with the vanilla villager experience sprite, scroller and two action buttons.
 */
public final class StatueOfferingScreen extends AbstractContainerScreen<StatueOfferingMenu> {
    private static final int WIDTH = 454;
    private static final int HEIGHT = 250;
    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 18;
    private static final int PREVIEW_WIDTH = 241;
    private static final int PREVIEW_HEIGHT = 222;
    private static final int TRADE_X = 262;
    private static final int TRADE_Y = 46;
    private static final int TRADE_WIDTH = 180;
    private static final int TRADE_ROWS = 6;
    private static final int TRADE_ROW_HEIGHT = 26;
    private static final int DARK = 0xff404040;
    private static final int MUTED = 0xff747474;
    private static final int GREEN = 0xff207a21;
    private static final int BACK = 0xffc6c6c6;
    private static final int ROW = 0xff999999;
    private static final int SELECTED = 0xffdddddd;

    //? if >=26.3 {
    /*private static final Identifier EXP_BACKGROUND = Identifier.withDefaultNamespace("container/villager/experience_bar_background");
    private static final Identifier EXP_CURRENT = Identifier.withDefaultNamespace("container/villager/experience_bar_current");
    private static final Identifier ARROW = Identifier.withDefaultNamespace("container/villager/trade_arrow");
    private static final Identifier SCROLL = Identifier.withDefaultNamespace("container/villager/scroller");
    *///?} else if =1.21.1 {
    private static final ResourceLocation EXP_BACKGROUND = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_background");
    private static final ResourceLocation EXP_CURRENT = ResourceLocation.withDefaultNamespace("container/villager/experience_bar_current");
    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/villager/trade_arrow");
    private static final ResourceLocation SCROLL = ResourceLocation.withDefaultNamespace("container/villager/scroller");
    //?} else {
    /*private static final ResourceLocation VILLAGER = new ResourceLocation("minecraft", "textures/gui/container/villager.png");
    *///?}

    private int firstLevel = 1;
    private boolean draggingScroller;
    private Button levelButton;
    private Button allButton;
    private StatuePreviewScene.Snapshot scene;
    private BlockPos sceneAltar;
    private long lastSceneTick = Long.MIN_VALUE;

    public StatueOfferingScreen(StatueOfferingMenu menu, Inventory inventory, Component title) {
        //? if >=26.3 {
        /*super(menu, inventory, title, WIDTH, HEIGHT);
        *///?} else {
        super(menu, inventory, title);
        this.imageWidth = WIDTH;
        this.imageHeight = HEIGHT;
        //?}
        this.inventoryLabelY = 10000;
    }

    @Override
    protected void init() {
        super.init();
        int right = leftPos + TRADE_X;
        this.levelButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.genshinecraft.offering.level_up"),
                button -> action(0)).bounds(right, topPos + 224, 86, 20).build());
        this.allButton = addRenderableWidget(Button.builder(
                Component.translatable("screen.genshinecraft.offering.all"),
                button -> action(1)).bounds(right + 94, topPos + 224, 86, 20).build());
        levelButton.setTooltip(net.minecraft.client.gui.components.Tooltip.create(
                Component.translatable("screen.genshinecraft.offering.tooltip.level_up")));
        allButton.setTooltip(net.minecraft.client.gui.components.Tooltip.create(
                Component.translatable("screen.genshinecraft.offering.tooltip.all")));
    }

    private void action(int operation) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, operation);
        }
    }

    private int lastStart() {
        return (int) Math.max(1L, Math.min(Integer.MAX_VALUE,
                StatueOfferings.unlocked(menu.level()) - TRADE_ROWS + 1));
    }

    private boolean insideScroller(double mx, double my) {
        int rx = (int) mx - leftPos, ry = (int) my - topPos;
        return rx >= TRADE_X + TRADE_WIDTH - 12 && rx < TRADE_X + TRADE_WIDTH
                && ry >= TRADE_Y && ry < TRADE_Y + TRADE_ROWS * TRADE_ROW_HEIGHT;
    }

    private void moveScroller(double my) {
        int range = Math.max(0, lastStart() - 1);
        int track = TRADE_ROWS * TRADE_ROW_HEIGHT - 27;
        double fraction = (my - topPos - TRADE_Y - 13.5) / Math.max(1, track);
        firstLevel = 1 + (int) Math.round(Math.max(0, Math.min(1, fraction)) * range);
    }

    private void scroll(double mouseX, double mouseY, double amount) {
        int rx = (int) mouseX - leftPos, ry = (int) mouseY - topPos;
        if (rx < TRADE_X || rx > TRADE_X + TRADE_WIDTH ||
                ry < TRADE_Y || ry >= TRADE_Y + TRADE_ROWS * TRADE_ROW_HEIGHT) return;
        if (amount == 0) return;
        firstLevel = (int) Math.max(1L,
                Math.min(lastStart(), (long) firstLevel + (amount > 0 ? -1 : 1)));
    }

    //? if =1.20.1 {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll(mouseX, mouseY, amount);
        return amount != 0;
    }
    *///?} else {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        scroll(mouseX, mouseY, vertical);
        return vertical != 0;
    }
    //?}

    //? if >=26.3 {
    /*@Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && insideScroller(event.x(), event.y())) {
            draggingScroller = true;
            moveScroller(event.y());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dx, double dy) {
        if (draggingScroller) {
            moveScroller(event.y());
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        if (draggingScroller) {
            draggingScroller = false;
            return true;
        }
        return super.mouseReleased(event);
    }
    *///?} else {
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && insideScroller(mx, my)) {
            draggingScroller = true;
            moveScroller(my);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (draggingScroller) {
            moveScroller(my);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (draggingScroller) {
            draggingScroller = false;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }
    //?}

    private interface Canvas {
        void fill(int x1, int y1, int x2, int y2, int color);
        void text(Component label, int x, int y, int color);
        void item(ItemStack stack, int x, int y, String overlay);
        default void item(ItemStack stack, int x, int y) { item(stack, x, y, null); }
        void arrow(int x, int y);
        void scroller(int x, int y);
        void experience(int x, int y, int width);
    }

    //? if >=26.3 {
    /*@Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        drawUi(new Canvas() {
            public void fill(int a, int b, int c, int d, int col) { graphics.fill(a, b, c, d, col); }
            public void text(Component label, int x, int y, int col) { graphics.text(font, label, x, y, col, false); }
            public void item(ItemStack stack, int x, int y, String overlay) {
                graphics.item(stack, x, y);
                graphics.itemDecorations(font, stack, x, y, overlay);
            }
            public void arrow(int x, int y) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, x, y, 10, 9); }
            public void scroller(int x, int y) { graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL, x, y, 6, 27); }
            public void experience(int x, int y, int width) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXP_BACKGROUND, x, y, 102, 5);
                if (width > 0) graphics.blitSprite(RenderPipelines.GUI_TEXTURED,
                        EXP_CURRENT, 102, 5, 0, 0, x, y, width, 5);
            }
        });
        submitPreview(graphics);
        Component info = hoverInfo(mouseX, mouseY);
        if (info != null) graphics.setTooltipForNextFrame(info, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // Everything is positioned on the actual native-style panel.
    }
    *///?} else {
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        drawUi(new Canvas() {
            public void fill(int a, int b, int c, int d, int col) { graphics.fill(a, b, c, d, col); }
            public void text(Component label, int x, int y, int col) { graphics.drawString(font, label, x, y, col, false); }
            public void item(ItemStack stack, int x, int y, String overlay) {
                graphics.renderItem(stack, x, y);
                graphics.renderItemDecorations(font, stack, x, y, overlay);
            }
            //? if =1.20.1 {
            /*public void arrow(int x, int y) { graphics.blit(VILLAGER, x, y, 0, 15f, 171f, 10, 9, 512, 256); }
            public void scroller(int x, int y) { graphics.blit(VILLAGER, x, y, 0, 0f, 199f, 6, 27, 512, 256); }
            public void experience(int x, int y, int width) {
                graphics.blit(VILLAGER, x, y, 0, 0f, 186f, 102, 5, 512, 256);
                if (width > 0) graphics.blit(VILLAGER, x, y, 0, 0f, 191f, width, 5, 512, 256);
            }
            *///?} else {
            public void arrow(int x, int y) { graphics.blitSprite(ARROW, x, y, 10, 9); }
            public void scroller(int x, int y) { graphics.blitSprite(SCROLL, x, y, 6, 27); }
            public void experience(int x, int y, int width) {
                graphics.blitSprite(EXP_BACKGROUND, x, y, 102, 5);
                if (width > 0) graphics.blitSprite(EXP_CURRENT, 102, 5, 0, 0, x, y, width, 5);
            }
            //?}
        });
        submitPreview(graphics);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        //? if =1.20.1 {
        /*renderBackground(graphics);
        *///?} else {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        //?}
        super.render(graphics, mouseX, mouseY, partialTick);
        Component info = hoverInfo(mouseX, mouseY);
        if (info != null) graphics.renderTooltip(font, info, mouseX, mouseY);
    }
    //?}

    private void drawUi(Canvas g) {
        int x = leftPos, y = topPos, right = x + TRADE_X;
        int current = menu.level();
        CarrierData.Element element = menu.element();
        levelButton.active = menu.availableSigils() >= menu.remainingToNextLevel()
                && current < Integer.MAX_VALUE;
        allButton.active = menu.availableSigils() > 0 && current < Integer.MAX_VALUE;

        // Original Minecraft container-edge pixel order: black outline, light top/left,
        // dark lower/right, untouched #c6c6c6 window interior. No stretched borders.
        g.fill(x, y, x + WIDTH, y + HEIGHT, 0xff000000);
        g.fill(x + 1, y + 1, x + WIDTH - 1, y + HEIGHT - 1, BACK);
        g.fill(x + 1, y + 1, x + WIDTH - 2, y + 2, 0xffffffff);
        g.fill(x + 1, y + 1, x + 2, y + HEIGHT - 2, 0xffffffff);
        g.fill(x + 1, y + HEIGHT - 2, x + WIDTH - 1, y + HEIGHT - 1, 0xff555555);
        g.fill(x + WIDTH - 2, y + 1, x + WIDTH - 1, y + HEIGHT - 1, 0xff555555);

        // Full-height left 3D viewport with vanilla-style inset edge.
        framedInset(g, x + PREVIEW_X, y + PREVIEW_Y, PREVIEW_WIDTH, PREVIEW_HEIGHT);
        g.fill(x + PREVIEW_X + 2, y + PREVIEW_Y + 2,
                x + PREVIEW_X + PREVIEW_WIDTH - 2,
                y + PREVIEW_Y + PREVIEW_HEIGHT - 2, 0xff393939);

        g.text(Component.translatable(menu.moon()
                ? "screen.genshinecraft.offering.moon_title"
                : "screen.genshinecraft.offering.title"), x + 10, y + 6, DARK);

        boolean moon = menu.moon();
        if (element == null && !moon) return;
        ItemStack sigil = new ItemStack(moon ? CarrierItems.MOON_SIGIL.get()
                : CarrierItems.SIGILS.get(element).get());
        String sigilName = sigil.getHoverName().getString();
        int xpX = right + (TRADE_WIDTH - 102) / 2;
        long nextCost = StatueOfferings.cost((long) current + 1);
        int xpWidth = (int) Math.round(Math.min(1d,
                (double) menu.contributed() / Math.max(1L, nextCost)) * 102);
        g.experience(xpX, y + 17, xpWidth);
        Component levelText = Component.translatable("screen.genshinecraft.offering.level_short", current);
        g.text(levelText, xpX + 51 - font.width(levelText) / 2, y + 6, DARK);
        g.text(Component.translatable("screen.genshinecraft.offering.owned",
                sigilName, menu.availableSigils()), right + 2, y + 32, DARK);

        long unlocked = StatueOfferings.unlocked(current);
        long lastStart = Math.max(1L, unlocked - TRADE_ROWS + 1);
        firstLevel = (int) Math.max(1L, Math.min(lastStart, firstLevel));

        for (int row = 0; row < TRADE_ROWS; row++) {
            long target = (long) firstLevel + row;
            if (target > unlocked || target > Integer.MAX_VALUE) break;
            int ry = y + TRADE_Y + row * TRADE_ROW_HEIGHT;
            boolean done = target <= current;
            boolean next = target == (long) current + 1;
            g.fill(right, ry, right + TRADE_WIDTH - 11, ry + 25,
                    next ? SELECTED : (row % 2 == 0 ? 0xffa8a8a8 : ROW));
            if (next) g.fill(right, ry, right + TRADE_WIDTH - 11, ry + 1, 0xffffffff);
            if (next) g.fill(right, ry, right + 1, ry + 25, 0xffffffff);
            if (next) g.fill(right, ry + 24, right + TRADE_WIDTH - 11, ry + 25, 0xff777777);
            long cost = target == 1 ? 0 : StatueOfferings.cost(target);
            g.item(sigil, right + 3, ry + 4, Long.toString(cost));
            g.arrow(right + 29, ry + 8);
            g.item(new ItemStack(Items.ENCHANTED_BOOK), right + 47, ry + 4,
                    Integer.toString(StatueOfferings.mastery((int) target)));
            g.item(new ItemStack(Items.EXPERIENCE_BOTTLE), right + 72, ry + 4,
                    Integer.toString(target == 1 ? 0 : StatueOfferings.experience((int) target)));
            if (!moon && target % 10 == 0) {
                g.item(new ItemStack(CarrierItems.MOON_SIGIL.get()), right + 97, ry + 4, "3");
            }
            if (moon) {
                g.item(new ItemStack(Items.EMERALD), right + 115, ry + 4,
                        target % 5 == 0 ? "4" : "1");
            }
            g.text(Component.literal("Lv." + target), right + 137, ry + 9,
                    next ? GREEN : DARK);
        }

        // Scroller uses exactly the same native sprite as villager trades.
        g.fill(right + TRADE_WIDTH - 8, y + TRADE_Y,
                right + TRADE_WIDTH - 2, y + TRADE_Y + TRADE_ROWS * TRADE_ROW_HEIGHT, 0xff777777);
        int range = (int) Math.max(1L, unlocked - TRADE_ROWS);
        int rail = (TRADE_ROWS * TRADE_ROW_HEIGHT) - 27;
        int scrollY = (int) (rail * (firstLevel - 1L) / range);
        g.scroller(right + TRADE_WIDTH - 8, y + TRADE_Y + scrollY);


    }

    private Component hoverInfo(int mouseX, int mouseY) {
        int rx = mouseX - leftPos, ry = mouseY - topPos;
        int current = menu.level();
        if (rx >= TRADE_X + (TRADE_WIDTH - 102) / 2
                && rx < TRADE_X + (TRADE_WIDTH + 102) / 2 && ry >= 17 && ry < 23) {
            return Component.translatable("screen.genshinecraft.offering.progress_short",
                    menu.contributed(), StatueOfferings.cost((long) current + 1));
        }
        if (rx >= TRADE_X + TRADE_WIDTH - 12 && rx < TRADE_X + TRADE_WIDTH
                && ry >= TRADE_Y && ry <= TRADE_Y + TRADE_ROWS * TRADE_ROW_HEIGHT) {
            return Component.translatable("screen.genshinecraft.offering.tooltip.scroll");
        }
        if (rx < TRADE_X || ry < TRADE_Y || ry >= TRADE_Y + TRADE_ROWS * TRADE_ROW_HEIGHT) {
            return null;
        }
        long target = (long) firstLevel + (ry - TRADE_Y) / TRADE_ROW_HEIGHT;
        if (target > StatueOfferings.unlocked(current) || target > Integer.MAX_VALUE) return null;
        int localX = rx - TRADE_X;
        int level = (int) target;
        var element = menu.element();
        String name = new ItemStack(menu.moon() ? CarrierItems.MOON_SIGIL.get()
                : CarrierItems.SIGILS.get(element).get()).getHoverName().getString();
        if (localX < 24) return Component.translatable("screen.genshinecraft.offering.tooltip.sigil",
                name, target == 1 ? 0 : StatueOfferings.cost(target));
        if (localX >= 27 && localX < 43) return Component.translatable(
                "screen.genshinecraft.offering.tooltip.next", target);
        if (localX >= 45 && localX < 65) return Component.translatable(
                "screen.genshinecraft.offering.tooltip.mastery", StatueOfferings.mastery(level));
        if (localX >= 70 && localX < 90) return Component.translatable(
                "screen.genshinecraft.offering.tooltip.xp", target == 1 ? 0 : StatueOfferings.experience(level));
        if (!menu.moon() && localX >= 95 && localX < 115 && target % 10 == 0) return Component.translatable(
                "screen.genshinecraft.offering.tooltip.moon", 3);
        if (menu.moon() && localX >= 114 && localX < 134) return Component.translatable(
                "screen.genshinecraft.offering.tooltip.emerald", target % 5 == 0 ? 4 : 1);
        return Component.translatable("screen.genshinecraft.offering.tooltip.level", target);
    }

    private static void framedInset(Canvas g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xff373737);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xff8b8b8b);
        g.fill(x + 2, y + 2, x + w - 2, y + h - 2, 0xff393939);
        g.fill(x + 1, y + h - 1, x + w, y + h, 0xffffffff);
        g.fill(x + w - 1, y + 1, x + w, y + h, 0xffffffff);
    }

    //? if >=26.3 {
    /*private void submitPreview(GuiGraphicsExtractor graphics) {
        var scene = sampleScene();
        if (scene != null) StatuePreviewRenderer.draw(graphics, scene,
                leftPos + PREVIEW_X + 2, topPos + PREVIEW_Y + 2,
                PREVIEW_WIDTH - 4, PREVIEW_HEIGHT - 4, currentYaw());
    }
    *///?} else {
    private void submitPreview(GuiGraphics graphics) {
        var scene = sampleScene();
        if (scene != null) StatuePreviewRenderer.draw(graphics, scene,
                leftPos + PREVIEW_X + 2, topPos + PREVIEW_Y + 2,
                PREVIEW_WIDTH - 4, PREVIEW_HEIGHT - 4, currentYaw());
    }
    //?}

    private StatuePreviewScene.Snapshot sampleScene() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || (menu.element() == null && !menu.moon())) return null;
        BlockPos altar = menu.statuePosition();
        if (!client.level.hasChunkAt(altar)) return null;
        long tick = client.level.getGameTime();
        if (scene == null || !altar.equals(sceneAltar) || tick - lastSceneTick >= 10 || tick < lastSceneTick) {
            scene = StatuePreviewScene.capture(client.level, altar);
            sceneAltar = altar;
            lastSceneTick = tick;
        }
        return scene;
    }

    private float currentYaw() {
        Minecraft client = Minecraft.getInstance();
        return client.level == null ? -35.0f :
                -35.0f + (client.level.getGameTime() % 720) * 0.5f;
    }
}
