package me.cyanhana.aef;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(Aef.MODID)
public class Aef {
    public static final String MODID = "aef";

    public Aef(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::registerKeyBindings);
        }
    }

    private void registerKeyBindings(final RegisterKeyMappingsEvent event) {
        ModKeyBindings.register(event);
    }

}
