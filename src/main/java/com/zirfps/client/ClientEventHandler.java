package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.event.RenderBlockEntityEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private int originalFramerateLimit = -1;
    private int tickCounter = 0;
    private double dynamicEntityRadiusSq = ZirConfig.entityRenderDistance * ZirConfig.entityRenderDistance;

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (!ZirConfig.enableEntityCulling || mc.player == null) return;
        
        Entity entity = event.getEntity();
        double dx = mc.player.getX() - entity.getX();
        double dy = mc.player.getY() - entity.getY();
        double dz = mc.player.getZ() - entity.getZ();
        
        if ((dx * dx + dy * dy + dz * dz) > dynamicEntityRadiusSq) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderBlockEntity(RenderBlockEntityEvent.Pre event) {
        if (!ZirConfig.enableEntityCulling || mc.player == null) return;

        BlockEntity be = event.getBlockEntity();
        double dx = mc.player.getX() - be.getBlockPos().getX();
        double dy = mc.player.getY() - be.getBlockPos().getY();
        double dz = mc.player.getZ() - be.getBlockPos().getZ();

        if ((dx * dx + dy * dy + dz * dz) > 2304.0) {
            event.setCanceled(true);
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
            double currentRadius = Math.sqrt(dynamicEntityRadiusSq);

            if (fps < ZirConfig.targetFps - 5) {
                currentRadius = Math.max(16.0, currentRadius - 4.0);
                if (mc.options.particles().get() == ParticleStatus.ALL) {
                    mc.options.particles().set(ParticleStatus.DECREASED);
                }
            } else if (fps > ZirConfig.targetFps + 10) {
                currentRadius = Math.min((double) ZirConfig.entityRenderDistance, currentRadius + 4.0);
                if (currentRadius > 48.0 && mc.options.particles().get() != ParticleStatus.ALL) {
                    mc.options.particles().set(ParticleStatus.ALL);
                }
            }
            dynamicEntityRadiusSq = currentRadius * currentRadius;
        }
    }
}
