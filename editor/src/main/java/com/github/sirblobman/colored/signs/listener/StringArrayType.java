package com.github.sirblobman.colored.signs.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class StringArrayType implements PersistentDataType<PersistentDataContainer, String[]> {
    private final Plugin plugin;

    public StringArrayType(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<String[]> getComplexType() {
        return String[].class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(String @NotNull [] complex,
                                                        @NotNull PersistentDataAdapterContext context) {
        NamespacedKey lengthKey = getKey("length");
        PersistentDataContainer primitive = context.newPersistentDataContainer();
        primitive.set(lengthKey, INTEGER, complex.length);

        for (int i = 0; i < complex.length; i++) {
            String part = complex[i];
            if (part == null) {
                continue;
            }

            String partId = Integer.toString(i);
            NamespacedKey partKey = getKey(partId);
            primitive.set(partKey, STRING, part);
        }

        return primitive;
    }

    @Override
    public String @NotNull [] fromPrimitive(@NotNull PersistentDataContainer primitive,
                                            @NotNull PersistentDataAdapterContext context) {
        NamespacedKey lengthKey = getKey("length");
        int length = primitive.getOrDefault(lengthKey, INTEGER, 0);
        if (length <= 0) {
            return new String[0];
        }

        String[] complex = new String[length];
        for (int i = 0; i < length; i++) {
            NamespacedKey partKey = getKey(Integer.toString(i));
            String part = primitive.get(partKey, STRING);
            complex[i] = part;
        }

        return complex;
    }

    private @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    private @NotNull NamespacedKey getKey(@NotNull String name) {
        Plugin plugin = getPlugin();
        return new NamespacedKey(plugin, name);
    }
}
