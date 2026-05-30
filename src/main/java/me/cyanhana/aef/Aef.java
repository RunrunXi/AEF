package me.cyanhana.aef;

import me.cyanhana.aef.client.gui.FavoritesKeys;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Aef.MODID)
public class Aef {
    public static final String MODID = "aef";
    public static final Logger LOGGER = LoggerFactory.getLogger(Aef.class);

    public Aef(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::registerKeyBindings);
        }
    }

    private void registerKeyBindings(final RegisterKeyMappingsEvent event) {
        ModKeyBindings.register(event);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        /**
         * 客户端设置事件处理。
         * @param event FML客户端设置事件。
         */
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            FavoritesKeys.init();
        }
    }
}
