package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiEventHandler {
    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof VideoSettingsScreen)) return;
        Minecraft mc = Minecraft.getInstance();
        event.addWidget(new Button(
            mc.mainWindow.getScaledWidth() / 2 - 100,
            mc.mainWindow.getScaledHeight() / 6 + 168,
            200, 20,
            new StringTextComponent("ZirFPS Settings..."),
            b -> mc.displayGuiScreen(new ZirConfigScreen(event.getGui()))
        ));
    }
}
