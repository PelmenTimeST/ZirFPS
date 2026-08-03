package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private int originalFramerateLimit = -1;
    private int tickCounter = 0;
    private double dynamicEntityRadius = ZirConfig.entityRenderDistance;

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (ZirConfig.enableEntityCulling && mc.player != null) {
            if (mc.player.distanceToSqr(event.getEntity()) > (dynamicEntityRadius * dynamicEntityRadius)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (mc.player == null) return;


        if (ZirConfig.enableDynamicFps) {
            boolean isFocused = mc.isWindowActive();
            if (!isFocused && originalFramerateLimit == -1) {
                originalFramerateLimit = mc.options.framerateLimit().get();
                mc.options.framerateLimit().set(ZirConfig.backgroundFpsLimit);
            } else if (isFocused && originalFramerateLimit != -1) {
                mc.options.framerateLimit().set(originalFramerateLimit);
                originalFramerateLimit = -1;
            }
        }


        if (ZirConfig.smartMode && ++tickCounter >= 20) {
            tickCounter = 0;
            int fps = mc.getFps();

            if (fps < ZirConfig.targetFps - 5) {

                dynamicEntityRadius = Math.max(16.0, dynamicEntityRadius - 4.0);
                if (mc.options.particles().get() == ParticleStatus.ALL) mc.options.particles().set(ParticleStatus.DECREASED);
            } else if (fps > ZirConfig.targetFps + 10) {

                dynamicEntityRadius = Math.min((double) ZirConfig.entityRenderDistance, dynamicEntityRadius + 4.0);
                if (dynamicEntityRadius > 48.0 && mc.options.particles().get() != ParticleStatus.ALL) mc.options.particles().set(ParticleStatus.ALL);
            }
        }
    }
}
