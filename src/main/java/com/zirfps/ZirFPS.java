package com.zirfps;

import com.zirfps.client.ClientEventHandler;
import com.zirfps.client.GuiEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("zirfps")
public class ZirFPS {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "zirfps";

    public ZirFPS() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
        LOGGER.info("ZirFPS loaded.");
    }
}
