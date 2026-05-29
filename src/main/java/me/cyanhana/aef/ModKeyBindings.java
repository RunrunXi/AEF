package me.cyanhana.aef;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ModKeyBindings {
    public static final String CATEGORY = "key.categories.aef";

    // 收藏物品按键绑定
    public static final KeyMapping FAVORITE_ITEM = new KeyMapping(
            "key.aef.favorite_item",
            KeyConflictContext.IN_GAME,
            KeyModifier.SHIFT,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            CATEGORY
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(FAVORITE_ITEM);
    }
}
