package com.zirfps;

import com.zirfps.client.ClientEventHandler;
import com.zirfps.client.GuiEventHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("zirfps")
public class ZirFPS {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "zirfps";

    public ZirFPS(IEventBus modEventBus) {
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new GuiEventHandler());
        LOGGER.info("ZirFPS 1.21.1 loaded. Smart Mode, Entity Culling, Chunk Occlusion active.");
    }
}
