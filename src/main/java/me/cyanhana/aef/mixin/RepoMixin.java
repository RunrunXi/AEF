package me.cyanhana.aef.mixin;

import appeng.client.gui.me.common.Repo;
import appeng.menu.me.common.GridInventoryEntry;
import me.cyanhana.aef.client.gui.FavoritesKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Repo.class)
public class RepoMixin {
    @Shadow
    private final ArrayList<GridInventoryEntry> view = new ArrayList<>();

    @Inject(method = "updateView", at = @At(
            value = "INVOKE",
            target = "Ljava/util/ArrayList;sort(Ljava/util/Comparator;)V",
            ordinal = 1,  // 第二个 sort 调用（view 的排序）
            shift = At.Shift.AFTER
    ))
    public final void afterViewSort(CallbackInfo ci) {
        // 收集所有收藏物品（保持它们在排序后的相对顺序）
        ArrayList<GridInventoryEntry> favorites = new ArrayList<>();
        for (GridInventoryEntry entry : view) {
            if (FavoritesKeys.isFavoritesKey(entry.getWhat())) {
                favorites.add(entry);
            }
        }

        // 如果有收藏物品，将它们移到最前面
        if (!favorites.isEmpty()) {
            // 移除所有收藏物品
            view.removeAll(favorites);

            // 在最前面插入收藏物品
            view.addAll(0, favorites);
        }
    }

}
