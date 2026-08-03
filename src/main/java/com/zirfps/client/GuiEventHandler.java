package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class GuiEventHandler {
    @SubscribeEvent
    public void onGuiInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof VideoSettingsScreen)) return;
        Minecraft mc = Minecraft.getInstance();
        event.addListener(Button.builder(
                Component.literal("ZirFPS Settings..."),
                b -> mc.setScreen(new ZirConfigScreen(event.getScreen()))
            )
            .pos(mc.getWindow().getGuiScaledWidth() / 2 - 100, mc.getWindow().getGuiScaledHeight() / 6 + 168)
            .size(200, 20)
            .build()
        );
    }
}
