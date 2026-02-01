package com.goodwitchlalya.lalyan_cosmetic_core.component;

import com.goodwitchlalya.lalyan_cosmetic_core.util.AttachmentsRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

public class EquippedCosmetic {
    private String id;
    private String variant;
    private AttachmentsRegistry.Colour colour;

    public static final BuilderCodec<EquippedCosmetic> CODEC = BuilderCodec.builder(EquippedCosmetic.class, EquippedCosmetic::new)
            .append(new KeyedCodec<>("Id", Codec.STRING), (data, value) -> data.id = value, (data) -> data.id).add()
            .append(new KeyedCodec<>("Variant", Codec.STRING), (data, value) -> data.variant = value, (data) -> data.variant).add()
            .append(new KeyedCodec<>("Colour", AttachmentsRegistry.Colour.CODEC), (data, value) -> data.colour = value, (data) -> data.colour).add()
            .build();

    public static ArrayCodec<EquippedCosmetic> ARRAY_CODEC = new ArrayCodec<>(CODEC, EquippedCosmetic[]::new);

    public EquippedCosmetic() {}

    public EquippedCosmetic(String id, String variant, AttachmentsRegistry.Colour colour) {
        this.id = id;
        this.variant = variant;
        this.colour = colour;
    }

    public boolean isVariant() {
        return variant == null;
    }

    public boolean isColorVariant() {
        return colour != null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EquippedCosmetic other && other.id.equals(this.id) && other.variant.equals(this.variant) && other.colour.equals(this.colour);
    }

    public String getId() {
        return id;
    }

    public String getVariant() {
        return variant;
    }

    public AttachmentsRegistry.Colour getColour() {
        return colour;
    }
}
