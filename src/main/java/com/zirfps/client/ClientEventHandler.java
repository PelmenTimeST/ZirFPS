package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (ZirConfig.enableDynamicFps) handleDynamicFps();
        if (ZirConfig.enableChunkOcclusion) ChunkOcclusionManager.update();
        if (ZirConfig.enableAdaptiveRenderDistance || ZirConfig.smartMode) handleAdaptiveRenderDistance();
        if (ZirConfig.smartMode) handleSmartMode();
    }

    private void handleDynamicFps() {
        GameSettings gs = mc.gameSettings;
        if (!mc.isGameFocused()) {
            if (savedFpsLimit < 0) {
                savedFpsLimit = gs.framerateLimit;
                gs.framerateLimit = ZirConfig.backgroundFpsLimit;
            }
        } else {
            if (savedFpsLimit >= 0) {
                gs.framerateLimit = savedFpsLimit;
                savedFpsLimit = -1;
            }
        }
    }

    private void handleAdaptiveRenderDistance() {
        GameSettings gs = mc.gameSettings;
        if (originalRenderDistance < 0) originalRenderDistance = Math.min(gs.renderDistanceChunks, ZirConfig.maxRenderDistance);
        if (gs.renderDistanceChunks > ZirConfig.maxRenderDistance) gs.renderDistanceChunks = ZirConfig.maxRenderDistance;

        tickCounter++;
        fpsAccumulator += Minecraft.getDebugFPS();
        if (tickCounter < 40) return;

        int avgFps = fpsAccumulator / tickCounter;
        tickCounter = 0;
        fpsAccumulator = 0;

        if (avgFps < ZirConfig.targetFps - 10 && gs.renderDistanceChunks > 4) {
            gs.renderDistanceChunks--;
        } else if (avgFps > ZirConfig.targetFps + 10 && gs.renderDistanceChunks < originalRenderDistance) {
            gs.renderDistanceChunks++;
        }
    }

    private void handleSmartMode() {
        if (mc.player == null) return;
        int tick = mc.player.ticksExisted;
        if (tick - lastSmartTick < 60) return;
        lastSmartTick = tick;

        int fps = Minecraft.getDebugFPS();
        GameSettings gs = mc.gameSettings;

        if (fps < ZirConfig.targetFps - 15) {
            if (gs.renderDistanceChunks > 6) gs.renderDistanceChunks--;
            else if (gs.entityShadows) gs.entityShadows = false;
            else if (gs.particles != ParticleStatus.MINIMAL) gs.particles = gs.particles == ParticleStatus.ALL ? ParticleStatus.DECREASED : ParticleStatus.MINIMAL;
            else if (gs.fancyGraphics) gs.fancyGraphics = false;
        } else if (fps > ZirConfig.targetFps + 20) {
            if (!gs.fancyGraphics) gs.fancyGraphics = true;
            else if (gs.particles == ParticleStatus.MINIMAL) gs.particles = ParticleStatus.DECREASED;
            else if (gs.particles == ParticleStatus.DECREASED) gs.particles = ParticleStatus.ALL;
            else if (!gs.entityShadows) gs.entityShadows = true;
            else if (gs.renderDistanceChunks < ZirConfig.maxRenderDistance) gs.renderDistanceChunks++;
        }
    }
}
