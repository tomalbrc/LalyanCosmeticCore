package com.goodwitchlalya.lalyan_cosmetic_core.component;

import com.goodwitchlalya.lalyan_cosmetic_core.util.AttachmentsRegistry;
import com.goodwitchlalya.lalyan_cosmetic_core.util.CustomCosmetic;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.List;

/**
 * A custom component responsible for storing a player's equipped cosmetic data.
 * This data is attached to the player's entity and persists across sessions.
 */
public class CosmeticComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, CosmeticComponent> TYPE;
    private List<EquippedCosmetic> internal = new ArrayList<>();

    public static ComponentType<EntityStore, CosmeticComponent> getComponentType() {
        return TYPE;
    }

    public static void setup(ComponentRegistryProxy<EntityStore> entityStoreRegistry) {
        CosmeticComponent.TYPE = entityStoreRegistry.registerComponent(CosmeticComponent.class, "CustomCosmeticData", CosmeticComponent.CODEC);
    }

    public boolean isSlotUsed(AttachmentsRegistry.CosmeticSlot slot) {
        for (var c : getCosmetics()) {
            if (AttachmentsRegistry.get(c.getId()).slot().equals(slot))
                return true;
        }

        return false;
    }

    /**
     * Codec for serializing and deserializing the component's data.
     * This allows the server to save and load the player's cosmetic choices.
     * It maps the "internal" array to a "Cosmetics" key in the saved data.
     */
    public static final BuilderCodec<CosmeticComponent> CODEC = BuilderCodec.builder(CosmeticComponent.class, CosmeticComponent::new)
            .append(new KeyedCodec<>("Values", EquippedCosmetic.ARRAY_CODEC), (data, value) -> data.internal = new ArrayList<>(List.of(value)), (data) -> data.internal.toArray(new EquippedCosmetic[0]))
            .add()
            .build();

    /**
     * Creates a deep copy of this component.
     * This is required by the component system.
     *
     * @return A new CosmeticData instance with the same data.
     */
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        CosmeticComponent data = new CosmeticComponent();
        data.internal.addAll(internal);
        return data;
    }

    /**
     * Returns an immutable list of the equipped cosmetic IDs.
     *
     * @return A List<String> containing the cosmetic IDs.
     */
    public List<EquippedCosmetic> getCosmetics() {
        return internal;
    }

    /**
     * Adds a cosmetic ID to the list of equipped cosmetics.
     *
     * @param customCosmetic The unique string identifier of the cosmetic to add.
     */
    public void addCosmetic(CustomCosmetic customCosmetic, String variant, AttachmentsRegistry.Colour colour) {
        internal.add(new EquippedCosmetic(customCosmetic.getId(), variant, colour));
    }

    /**
     * Removes a cosmetic ID from the list of equipped cosmetics.
     *
     * @param key The unique string identifier of the cosmetic to remove.
     */
    public void removeCosmetic(AttachmentsRegistry.CosmeticSlot key) {
        internal.removeIf(x -> key.equals(AttachmentsRegistry.get(x.getId()).slot));
    }

    public void removeCosmetic(EquippedCosmetic equippedCosmetic) {
        internal.removeIf(x -> x.getId().equals(equippedCosmetic.getId()) && ((x.getVariant() != null && x.getVariant().equals(equippedCosmetic.getVariant()) || (x.getColour() != null && x.getColour().equals(equippedCosmetic.getColour())))));
    }

    public void removeCosmetic(String id, String variant, AttachmentsRegistry.Colour colour) {
        internal.removeIf(x -> x.getId().equals(id) && ((x.getVariant() != null && x.getVariant().equals(variant) || (x.getColour() != null && x.getColour().equals(colour)))));
    }

    public CustomCosmetic getCosmetic(AttachmentsRegistry.CosmeticSlot slot) {
        if (internal == null) return null;

        for (var cosmetic : internal) {
            var c = AttachmentsRegistry.get(cosmetic.getId());

            if (c != null && c.slot() == slot) {
                return c;
            }
        }

        return null;
    }
}
