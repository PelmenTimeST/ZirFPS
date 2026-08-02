package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private int savedFpsLimit = -1;

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
    }

    private void handleDynamicFps() {
        GameSettings gs = mc.gameSettings;
        boolean focused = mc.isGameFocused();

        if (!focused) {
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
}package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.GameOptions;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    private final Minecraft mc = Minecraft.getInstance();
    private int savedFpsLimit = -1;

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
    }

    private void handleDynamicFps() {
        GameOptions options = mc.options;
        boolean focused = mc.isGameFocused();

        if (!focused) {
            if (savedFpsLimit < 0) {
                savedFpsLimit = options.framerateLimit;
                options.framerateLimit = ZirConfig.backgroundFpsLimit;
            }
        } else {
            if (savedFpsLimit >= 0) {
                options.framerateLimit = savedFpsLimit;
                savedFpsLimit = -1;
            }
        }
    }
}
