package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.util.text.StringTextComponent;

public class ZirConfigScreen extends Screen {
    private final Screen parent;

    protected ZirConfigScreen(Screen parent) {
        super(new StringTextComponent("ZirFPS Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = width / 2 - 100;
        int y = height / 6;
        int s = 24;

        addButton(new Button(x, y, 200, 20, new StringTextComponent("Smart Mode: " + onOff(ZirConfig.smartMode)), b -> {
            ZirConfig.smartMode = !ZirConfig.smartMode;
            b.setMessage(new StringTextComponent("Smart Mode: " + onOff(ZirConfig.smartMode)));
        }));

        addButton(new Button(x, y + s, 200, 20, new StringTextComponent("Target FPS: " + ZirConfig.targetFps), b -> {
            ZirConfig.targetFps = cycle(ZirConfig.targetFps, 30, 120, 15);
            b.setMessage(new StringTextComponent("Target FPS: " + ZirConfig.targetFps));
        }));

        addButton(new Button(x, y + s * 2, 200, 20, new StringTextComponent("Entity Culling: " + onOff(ZirConfig.enableEntityCulling)), b -> {
            ZirConfig.enableEntityCulling = !ZirConfig.enableEntityCulling;
            b.setMessage(new StringTextComponent("Entity Culling: " + onOff(ZirConfig.enableEntityCulling)));
        }));

        addButton(new Button(x, y + s * 3, 200, 20, new StringTextComponent("Chunk Occlusion: " + onOff(ZirConfig.enableChunkOcclusion)), b -> {
            ZirConfig.enableChunkOcclusion = !ZirConfig.enableChunkOcclusion;
            b.setMessage(new StringTextComponent("Chunk Occlusion: " + onOff(ZirConfig.enableChunkOcclusion)));
        }));

        addButton(new Button(x, y + s * 4, 200, 20, new StringTextComponent("Dynamic FPS: " + onOff(ZirConfig.enableDynamicFps)), b -> {
            ZirConfig.enableDynamicFps = !ZirConfig.enableDynamicFps;
            b.setMessage(new StringTextComponent("Dynamic FPS: " + onOff(ZirConfig.enableDynamicFps)));
        }));

        addButton(new Button(x, y + s * 5, 200, 20, new StringTextComponent("Background FPS: " + ZirConfig.backgroundFpsLimit), b -> {
            ZirConfig.backgroundFpsLimit = cycle(ZirConfig.backgroundFpsLimit, 1, 30, 1);
            b.setMessage(new StringTextComponent("Background FPS: " + ZirConfig.backgroundFpsLimit));
        }));

        addButton(new Button(x, y + s * 6, 200, 20, new StringTextComponent("Max Render Distance: " + ZirConfig.maxRenderDistance), b -> {
            ZirConfig.maxRenderDistance = cycle(ZirConfig.maxRenderDistance, 2, 32, 1);
            b.setMessage(new StringTextComponent("Max Render Distance: " + ZirConfig.maxRenderDistance));
        }));

        addButton(new Button(x, y + s * 7, 200, 20, new StringTextComponent("Entity Distance: " + ZirConfig.entityRenderDistance), b -> {
            ZirConfig.entityRenderDistance = cycle(ZirConfig.entityRenderDistance, 16, 256, 8);
            b.setMessage(new StringTextComponent("Entity Distance: " + ZirConfig.entityRenderDistance));
        }));

        addButton(new Button(x, y + s * 8, 200, 20, new StringTextComponent("Entity Shadows: " + onOff(ZirConfig.entityShadows)), b -> {
            ZirConfig.entityShadows = !ZirConfig.entityShadows;
            Minecraft.getInstance().gameSettings.entityShadows = ZirConfig.entityShadows;
            b.setMessage(new StringTextComponent("Entity Shadows: " + onOff(ZirConfig.entityShadows)));
        }));

        addButton(new Button(x, y + s * 9, 200, 20, new StringTextComponent("Particles: " + particleName(ZirConfig.particles)), b -> {
            ZirConfig.particles = cycle(ZirConfig.particles, 0, 2, 1);
            applyParticles();
            b.setMessage(new StringTextComponent("Particles: " + particleName(ZirConfig.particles)));
        }));

        addButton(new Button(x, height - 30, 200, 20, new StringTextComponent("Done"), b -> {
            Minecraft.getInstance().displayGuiScreen(parent);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        drawCenteredString(font, title.getString(), width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, partialTicks);
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
        Minecraft.getInstance().gameSettings.particles = ZirConfig.particles == 0 ? ParticleStatus.ALL : ZirConfig.particles == 1 ? ParticleStatus.DECREASED : ParticleStatus.MINIMAL;
    }
}
