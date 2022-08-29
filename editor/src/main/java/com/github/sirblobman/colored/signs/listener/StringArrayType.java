package com.github.sirblobman.colored.signs.listener;

import java.util.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;

public final class StringArrayType implements PersistentDataType<PersistentDataContainer, String[]> {
    private final JavaPlugin plugin;

    public StringArrayType(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null!");
    }

    @NotNull
    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @NotNull
    @Override
    public Class<String[]> getComplexType() {
        return String[].class;
    }

    @NotNull
    @Override
    public PersistentDataContainer toPrimitive(String[] complex, PersistentDataAdapterContext context) {
        NamespacedKey lengthKey = getKey("length");
        PersistentDataContainer primitive = context.newPersistentDataContainer();
        primitive.set(lengthKey, INTEGER, complex.length);

        for (int i = 0; i < complex.length; i++) {
            String part = complex[i];
            if(part == null) {
                continue;
            }

            String partId = Integer.toString(i);
            NamespacedKey partKey = getKey(partId);
            primitive.set(partKey, STRING, part);
        }

        return primitive;
    }

    @Override
    public String @NotNull [] fromPrimitive(PersistentDataContainer primitive,
                                            @NotNull PersistentDataAdapterContext context) {
        NamespacedKey lengthKey = getKey("length");
        int length = primitive.getOrDefault(lengthKey, INTEGER, 0);
        if (length <= 0) {
            return new String[0];
        }

        String[] complex = new String[length];
        for(int i = 0; i < length; i++) {
            NamespacedKey partKey = getKey(Integer.toString(i));
            String part = primitive.get(partKey, STRING);
            complex[i] = part;
        }

        return complex;
    }

    private JavaPlugin getPlugin() {
        return this.plugin;
    }

    private NamespacedKey getKey(String name) {
        JavaPlugin plugin = getPlugin();
        return new NamespacedKey(plugin, name);
    }
}
