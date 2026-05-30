package me.cyanhana.aef.client.gui;

import appeng.api.stacks.AEKey;
import java.util.HashSet;
import java.util.Set;

public final class FavoritesKeys {
    private static final Set<AEKey> favorites = new HashSet<>();

    public static Set<AEKey> getFavoritesKeys() {
        return favorites;
    }

    public static boolean isEmpty() {
        return favorites.isEmpty();
    }

    public static void clearFavoritesKeys() {
        favorites.clear();
    }

    public static void addFavoritesKey(AEKey key) {
        favorites.add(key);
    }

    public static void removeFavoritesKey(AEKey key) {
        favorites.remove(key);
    }

    public static boolean isFavoritesKey(AEKey what) {
        return favorites.contains(what);
    }
}
