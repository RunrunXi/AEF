package me.cyanhana.aef.client.gui;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public final class FavoritesKeys {
    private static final Set<AEKey> favorites = new HashSet<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "ae2_favorites.json";

    // 初始化时调用
    public static void init() {
        load();
    }

    public static Set<AEKey> getFavoritesKeys() {
        return favorites;
    }

    public static boolean isEmpty() {
        return favorites.isEmpty();
    }

    public static void clearFavoritesKeys() {
        favorites.clear();
        save();
    }

    public static void addFavoritesKey(AEKey key) {
        favorites.add(key);
        save();
    }

    public static void removeFavoritesKey(AEKey key) {
        favorites.remove(key);
        save();
    }

    public static boolean isFavoritesKey(AEKey what) {
        return favorites.contains(what);
    }

    // 保存：使用 GenericStack 作为中间格式
    public static void save() {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();

        for (AEKey key : favorites) {
            // 将 AEKey 包装成 GenericStack（数量随便填1，反正只用来存储key）
            GenericStack stack = new GenericStack(key, 1);

            // 使用 GenericStack 自带的 TAG_CODEC 转换为 NBT
            var tag = GenericStack.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, stack)
                    .getOrThrow();

            array.add(tag.toString());
        }

        root.add("favorites", array);

        try {
            Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载：从 GenericStack 恢复
    public static void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        if (!Files.exists(path)) return;

        try {
            String json = Files.readString(path);
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            JsonArray array = root.getAsJsonArray("favorites");

            favorites.clear();
            for (int i = 0; i < array.size(); i++) {
                String tagStr = array.get(i).getAsString();
                net.minecraft.nbt.Tag tag = net.minecraft.nbt.StringTag.valueOf(tagStr);

                // 从 NBT 解析 GenericStack
                var result = GenericStack.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, tag);
                result.result().ifPresent(stack -> {
                    favorites.add(stack.what());
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
