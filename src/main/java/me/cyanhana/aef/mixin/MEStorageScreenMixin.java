package me.cyanhana.aef.mixin;

import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.PinnedKeys;
import appeng.client.gui.me.common.Repo;
import appeng.client.gui.me.common.RepoSlot;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ISortSource;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import com.mojang.blaze3d.platform.InputConstants;
import me.cyanhana.aef.client.gui.FavoritesKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MEStorageScreen.class)
public abstract class MEStorageScreenMixin<C extends MEStorageMenu>
        extends AEBaseScreen<C> implements ISortSource {

    @Final
    @Shadow
    protected Repo repo;

    public MEStorageScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    /**
     * 在鼠标点击事件中拦截，检测是否按下了收藏按键（Shift+中键）
     */
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double xCoord, double yCoord, int btn, CallbackInfoReturnable<Boolean> cir) {
        // 检查是否是鼠标中键
        // 检查是否按下了Shift键
        if (Minecraft.getInstance().options.keyPickItem.matchesMouse(btn) && AEF$hasShiftDown()) {
            // 获取当前鼠标悬停的slot
            Slot slot = findSlot(xCoord, yCoord);

            // 如果是RepoSlot，说明是终端中的物品
            if (slot instanceof RepoSlot repoSlot) {
                GridInventoryEntry entry = repoSlot.getEntry();

                if (entry != null && entry.getWhat() != null) {
                    AEKey what = entry.getWhat();

                    // 切换收藏状态
                    if (FavoritesKeys.isFavoritesKey(what)) {
                        // 如果已收藏，取消收藏
                        FavoritesKeys.removeFavoritesKey(what);
                    } else {
                        // 如果未收藏，添加收藏
                        FavoritesKeys.addFavoritesKey(what);
                    }

                    // 更新视图以显示变化
                    repo.updateView();

                    // 取消事件，防止其他处理
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Unique
    private static boolean AEF$hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

}
