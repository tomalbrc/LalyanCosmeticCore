package com.goodwitchlalya.lalyan_cosmetic_core.component;

import com.goodwitchlalya.lalyan_cosmetic_core.util.AttachmentsRegistry;
import com.goodwitchlalya.lalyan_cosmetic_core.util.CustomCosmetic;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A custom component responsible for storing a player's equipped cosmetic data.
 * This data is attached to the player's entity and persists across sessions.
 */
public class UnlockedCosmeticsComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, UnlockedCosmeticsComponent> TYPE;
    private Set<String> internal = new ObjectArraySet<>();

    public static ComponentType<EntityStore, UnlockedCosmeticsComponent> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        UnlockedCosmeticsComponent.TYPE = entityStoreRegistry.registerComponent(UnlockedCosmeticsComponent.class, "UnlockedCosmeticData", UnlockedCosmeticsComponent.CODEC);
    }

    public static final BuilderCodec<UnlockedCosmeticsComponent> CODEC = BuilderCodec.builder(UnlockedCosmeticsComponent.class, UnlockedCosmeticsComponent::new)
            .append(new KeyedCodec<>("Values", BuilderCodec.STRING_ARRAY), (data, value) -> data.internal = new ObjectArraySet<>(List.of(value)), (data) -> data.internal.toArray(new String[0]))
            .add()
            .build();

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        UnlockedCosmeticsComponent data = new UnlockedCosmeticsComponent();
        data.internal.addAll(internal);
        return data;
    }

    public Set<String> all() {
        return internal;
    }

    public boolean add(String id) {
        return internal.add(id);
    }

    public void add(CustomCosmetic customCosmetic) {
        internal.add(customCosmetic.getId());
    }

    public boolean contains(String id) {
        return internal.contains(id);
    }

    public boolean removeCosmetic(String id) {
        return internal.remove(id);
    }
}
