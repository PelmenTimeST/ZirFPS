package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.GraphicsStatus;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.tick.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private int savedFpsLimit = -1;
    private int tickCounter = 0;
    private int fpsAccumulator = 0;
    private int originalRenderDistance = -1;
    private int lastSmartTick = 0;

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (!ZirConfig.enableEntityCulling) return;
        if (!CullingHelper.isVisible(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (ZirConfig.enableDynamicFps) handleDynamicFps();
        if (ZirConfig.enableChunkOcclusion) ChunkOcclusionManager.update();
        if (ZirConfig.enableAdaptiveRenderDistance || ZirConfig.smartMode) handleAdaptiveRenderDistance();
        if (ZirConfig.smartMode) handleSmartMode();
    }

    private void handleDynamicFps() {
        Options options = mc.options;
        if (!mc.getWindow().isFocused()) {
            if (savedFpsLimit < 0) {
                savedFpsLimit = options.framerateLimit().get();
                options.framerateLimit().set(ZirConfig.backgroundFpsLimit);
            }
        } else {
            if (savedFpsLimit >= 0) {
                options.framerateLimit().set(savedFpsLimit);
                savedFpsLimit = -1;
            }
        }
    }

    private void handleAdaptiveRenderDistance() {
        Options options = mc.options;
        if (originalRenderDistance < 0) originalRenderDistance = Math.min(options.renderDistance().get(), ZirConfig.maxRenderDistance);
        if (options.renderDistance().get() > ZirConfig.maxRenderDistance) options.renderDistance().set(ZirConfig.maxRenderDistance);

        tickCounter++;
        fpsAccumulator += Minecraft.getInstance().getFps();
        if (tickCounter < 40) return;

        int avgFps = fpsAccumulator / tickCounter;
        tickCounter = 0;
        fpsAccumulator = 0;

        if (avgFps < ZirConfig.targetFps - 10 && options.renderDistance().get() > 4) {
            options.renderDistance().set(options.renderDistance().get() - 1);
        } else if (avgFps > ZirConfig.targetFps + 10 && options.renderDistance().get() < originalRenderDistance) {
            options.renderDistance().set(options.renderDistance().get() + 1);
        }
    }

    private void handleSmartMode() {
        if (mc.player == null) return;
        int tick = mc.player.tickCount;
        if (tick - lastSmartTick < 60) return;
        lastSmartTick = tick;

        int fps = Minecraft.getInstance().getFps();
        Options options = mc.options;

        if (fps < ZirConfig.targetFps - 15) {
            if (options.renderDistance().get() > 6) {
                options.renderDistance().set(options.renderDistance().get() - 1);
            } else if (options.entityShadows().get()) {
                options.entityShadows().set(false);
            } else if (options.particles().get() != ParticleStatus.MINIMAL) {
                options.particles().set(options.particles().get() == ParticleStatus.ALL ? ParticleStatus.DECREASED : ParticleStatus.MINIMAL);
            } else if (options.graphicsMode().get() != GraphicsStatus.FAST) {
                options.graphicsMode().set(GraphicsStatus.FAST);
            }
        } else if (fps > ZirConfig.targetFps + 20) {
            if (options.graphicsMode().get() == GraphicsStatus.FAST) {
                options.graphicsMode().set(GraphicsStatus.FANCY);
            } else if (options.particles().get() == ParticleStatus.MINIMAL) {
                options.particles().set(ParticleStatus.DECREASED);
            } else if (options.particles().get() == ParticleStatus.DECREASED) {
                options.particles().set(ParticleStatus.ALL);
            } else if (!options.entityShadows().get()) {
                options.entityShadows().set(true);
            } else if (options.renderDistance().get() < ZirConfig.maxRenderDistance) {
                options.renderDistance().set(options.renderDistance().get() + 1);
            }
        }
    }
}
