package com.zirfps;

import com.zirfps.client.ClientEventHandler;
import com.zirfps.client.GuiEventHandler;
import com.zirfps.client.ZirConfigScreen;
import com.zirfps.config.ZirConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = ZirFPS.MOD_ID, dist = Dist.CLIENT)
public class ZirFPS {
    public static final String MOD_ID = "zirfps";

    public ZirFPS(IEventBus modEventBus, ModContainer modContainer) {

        modContainer.registerConfig(ModConfig.Type.CLIENT, ZirConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, screen) -> new ZirConfigScreen(screen));

        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new GuiEventHandler());
    }
}
