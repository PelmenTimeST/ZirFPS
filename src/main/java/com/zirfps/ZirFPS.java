package com.zirfps;

import com.zirfps.client.ClientEventHandler;
import com.zirfps.client.ZirConfigScreen;
import com.zirfps.config.ZirConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = ZirFPS.MOD_ID, dist = Dist.CLIENT)
public class ZirFPS {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "zirfps";

    public ZirFPS(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ZirConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> new ZirConfigScreen(screen));

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onConfigChanged);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        LOGGER.info("ZirFPS loaded. Smart Mode, Entity Culling, Chunk Occlusion active.");
    }

    private void onConfigChanged(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == ZirConfig.SPEC) {
            ZirConfig.bake();
        }
    }
}
