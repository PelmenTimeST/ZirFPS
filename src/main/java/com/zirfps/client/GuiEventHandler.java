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

        if (!(event.getScreen() instanceof OptionsScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        int screenWidth = event.getScreen().width;
        int screenHeight = event.getScreen().height;

        int buttonWidth = 150;
        int buttonHeight = 20;
        int x = screenWidth / 2 - 155; 
        int y = screenHeight - 27;     

        event.addListener(Button.builder(
                Component.literal("ZirFPS..."),
                b -> mc.setScreen(new ZirConfigScreen(event.getScreen()))
            )
            .pos(x, y)
            .size(buttonWidth, buttonHeight)
            .build()
        );
    }
}
