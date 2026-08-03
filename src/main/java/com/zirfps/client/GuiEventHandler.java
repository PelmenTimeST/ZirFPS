package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class GuiEventHandler {
    @SubscribeEvent
    public void onGuiInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof OptionsScreen screen)) return;

        int screenWidth = screen.width;
        int screenHeight = screen.height;

        int x = screenWidth / 2 + 104;
        int y = screenHeight - 27;
        int buttonWidth = 96;
        int buttonHeight = 20;

        event.addListener(Button.builder(
                Component.literal("⚡ ZirFPS"),
                b -> Minecraft.getInstance().setScreen(new ZirConfigScreen(screen))
            )
            .pos(x, y)
            .size(buttonWidth, buttonHeight)
            .build()
        );
    }
}
