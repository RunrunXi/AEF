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
import com.mojang.blaze3d.systems.RenderSystem;
import me.cyanhana.aef.client.gui.FavoritesKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MEStorageScreen.class, remap = false)
public abstract class MEStorageScreenMixin<C extends MEStorageMenu>
        extends AEBaseScreen<C> implements ISortSource {

    @Final
    @Shadow
    protected Repo repo;

    public MEStorageScreenMixin(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    /**
     * 检测是否按下了收藏按键（Shift+中键）
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

    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void beforeRenderSlot(GuiGraphics guiGraphics, Slot s, CallbackInfo ci) {
        if (s instanceof RepoSlot repoSlot) {
            GridInventoryEntry entry = repoSlot.getEntry();

            if (entry != null && entry.getWhat() != null) {
                AEKey what = entry.getWhat();

                // 如果是收藏物品，绘制半透明黄色背景
                if (FavoritesKeys.isFavoritesKey(what)) {
                    // 保存当前的混合状态
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();

                    // 设置半透明黄色 (RGBA: 255, 255, 0, 80)
                    int color = 0x50FFFF00;  // 0x50 = 80/255 透明度

                    // 绘制背景矩形
                    guiGraphics.fill(
                            s.x - 1,      // x1
                            s.y - 1,      // y1
                            s.x + 17,     // x2 (slot 是 16x16)
                            s.y + 17,     // y2
                            color         // 半透明黄色
                    );

                    // 恢复混合状态
                    RenderSystem.disableBlend();
                }
            }
        }
    }

    @Unique
    private static boolean AEF$hasShiftDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
    }

}
