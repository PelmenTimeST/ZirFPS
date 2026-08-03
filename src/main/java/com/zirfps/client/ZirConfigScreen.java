package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ZirConfigScreen extends Screen {
    private final Screen parent;
    private int scrollOffset = 0;
    private static final int BUTTON_HEIGHT = 24;
    private static final int CONTENT_HEIGHT = 11 * BUTTON_HEIGHT + 60;
    private final List<Button> configButtons = new ArrayList<>();

    protected ZirConfigScreen(Screen parent) {
        super(Component.literal("ZirFPS Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        configButtons.clear();
        int x = width / 2 - 100;
        int y = height / 6;

        configButtons.add(makeButton(x, y, "Smart Mode: " + onOff(ZirConfig.smartMode), b -> {
            ZirConfig.smartMode = !ZirConfig.smartMode;
            b.setMessage(Component.literal("Smart Mode: " + onOff(ZirConfig.smartMode)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT, "Target FPS: " + ZirConfig.targetFps, b -> {
            ZirConfig.targetFps = cycle(ZirConfig.targetFps, 30, 120, 15);
            b.setMessage(Component.literal("Target FPS: " + ZirConfig.targetFps));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 2, "Entity Culling: " + onOff(ZirConfig.enableEntityCulling), b -> {
            ZirConfig.enableEntityCulling = !ZirConfig.enableEntityCulling;
            b.setMessage(Component.literal("Entity Culling: " + onOff(ZirConfig.enableEntityCulling)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 3, "Chunk Occlusion: " + onOff(ZirConfig.enableChunkOcclusion), b -> {
            ZirConfig.enableChunkOcclusion = !ZirConfig.enableChunkOcclusion;
            b.setMessage(Component.literal("Chunk Occlusion: " + onOff(ZirConfig.enableChunkOcclusion)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 4, "Adaptive Distance: " + onOff(ZirConfig.enableAdaptiveRenderDistance), b -> {
            ZirConfig.enableAdaptiveRenderDistance = !ZirConfig.enableAdaptiveRenderDistance;
            b.setMessage(Component.literal("Adaptive Distance: " + onOff(ZirConfig.enableAdaptiveRenderDistance)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 5, "Dynamic FPS: " + onOff(ZirConfig.enableDynamicFps), b -> {
            ZirConfig.enableDynamicFps = !ZirConfig.enableDynamicFps;
            b.setMessage(Component.literal("Dynamic FPS: " + onOff(ZirConfig.enableDynamicFps)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 6, "Background FPS: " + ZirConfig.backgroundFpsLimit, b -> {
            ZirConfig.backgroundFpsLimit = cycle(ZirConfig.backgroundFpsLimit, 1, 30, 1);
            b.setMessage(Component.literal("Background FPS: " + ZirConfig.backgroundFpsLimit));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 7, "Max Render Distance: " + ZirConfig.maxRenderDistance, b -> {
            ZirConfig.maxRenderDistance = cycle(ZirConfig.maxRenderDistance, 2, 32, 1);
            b.setMessage(Component.literal("Max Render Distance: " + ZirConfig.maxRenderDistance));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 8, "Entity Distance: " + ZirConfig.entityRenderDistance, b -> {
            ZirConfig.entityRenderDistance = cycle(ZirConfig.entityRenderDistance, 16, 256, 8);
            b.setMessage(Component.literal("Entity Distance: " + ZirConfig.entityRenderDistance));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 9, "Entity Shadows: " + onOff(ZirConfig.entityShadows), b -> {
            ZirConfig.entityShadows = !ZirConfig.entityShadows;
            Minecraft.getInstance().options.entityShadows().set(ZirConfig.entityShadows);
            b.setMessage(Component.literal("Entity Shadows: " + onOff(ZirConfig.entityShadows)));
        }));

        configButtons.add(makeButton(x, y + BUTTON_HEIGHT * 10, "Particles: " + particleName(ZirConfig.particles), b -> {
            ZirConfig.particles = cycle(ZirConfig.particles, 0, 2, 1);
            applyParticles();
            b.setMessage(Component.literal("Particles: " + particleName(ZirConfig.particles)));
        }));

        configButtons.forEach(this::addRenderableWidget);

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> Minecraft.getInstance().setScreen(parent))
            .pos(x, height - 30)
            .size(200, 20)
            .build());

        updateButtonPositions();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, CONTENT_HEIGHT - height + height / 6);
        scrollOffset = Mth.clamp(scrollOffset - (int)(scrollY * 12), 0, maxScroll);
        updateButtonPositions();
        return true;
    }

    private void updateButtonPositions() {
        int baseY = height / 6 - scrollOffset;
        for (int i = 0; i < configButtons.size(); i++) {
            configButtons.get(i).setY(baseY + i * BUTTON_HEIGHT);
        }
    }

    private Button makeButton(int x, int y, String text, Button.OnPress onPress) {
        return Button.builder(Component.literal(text), onPress).pos(x, y).size(200, 20).build();
    }

    private static String onOff(boolean v) {
        return v ? "ON" : "OFF";
    }

    private static int cycle(int v, int min, int max, int step) {
        v += step;
        return v > max ? min : v;
    }

    private static String particleName(int v) {
        return v == 0 ? "All" : v == 1 ? "Decreased" : "Minimal";
    }

    private static void applyParticles() {
        Minecraft.getInstance().options.particles().set(
            ZirConfig.particles == 0 ? ParticleStatus.ALL :
            ZirConfig.particles == 1 ? ParticleStatus.DECREASED : ParticleStatus.MINIMAL
        );
    }
}
