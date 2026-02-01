package com.goodwitchlalya.lalyan_cosmetic_core.util;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.*;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.server.core.asset.common.CommonAssetValidator;
import com.hypixel.hytale.server.core.asset.type.item.config.AssetIconProperties;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.ItemExistsValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// A class holding all the data for a single attachment, loaded from asset files or JSON.
// This includes paths to model, texture, icon, as well as variants and slot overrides.
public class CustomCosmetic implements JsonAssetWithMap<String, DefaultAssetMap<String, CustomCosmetic>> {
    private String id;
    private String itemId;
    private String name = "Cosmetic";
    private String model;
    private String texture;
    private String icon;
    private AttachmentsRegistry.Alternative alternatives;
    private List<AttachmentsRegistry.CosmeticSlot> slotOverrides;
    private AttachmentsRegistry.Colour defaultColor;

    public AttachmentsRegistry.CosmeticSlot slot = AttachmentsRegistry.CosmeticSlot.Head; // The primary slot this attachment belongs to.

    protected AssetExtraInfo.Data extraData;

    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(CustomCosmetic::getAssetStore));

    public static final AssetBuilderCodec<String, CustomCosmetic> CODEC = AssetBuilderCodec.builder(CustomCosmetic.class, CustomCosmetic::new, Codec.STRING, (a, b) -> a.id = b, (a) -> a.id, (a, b) -> a.extraData = b, (a) -> a.extraData)
            .metadata(new UIEditorPreview(UIEditorPreview.PreviewType.MODEL))
            .metadata(new UITypeIcon("Item.png"))
            .metadata(new UIRebuildCaches(false, UIRebuildCaches.ClientCache.MODELS, UIRebuildCaches.ClientCache.MODEL_TEXTURES, UIRebuildCaches.ClientCache.ITEM_ICONS))

            .appendInherited(new KeyedCodec<>("Icon", Codec.STRING),
                    (item, s) -> item.icon = s,
                    item -> item.icon,
                    (item, parent) -> item.icon = parent.icon)
            .addValidator(CommonAssetValidator.ICON_ITEM)
            .metadata(new UIEditor(new UIEditor.Icon("Icons/ItemsGenerated/{assetId}.png", 64, 64)))
            .metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODEL_TEXTURES))
            .add()

            .appendInherited(new KeyedCodec<>("Voucher Item", Codec.STRING), (data, value) -> data.itemId = value, (data) -> data.itemId, (data, parent) -> data.itemId = parent.itemId)
            .documentation("Item id for the voucher item when withdrawing this cosmetic")
            .addValidator(Item.VALIDATOR_CACHE.getValidator())
            .add()

            .appendInherited(new KeyedCodec<>("Name", Codec.STRING), (data, value) -> data.name = value, (data) -> data.name, (data, parent) -> data.name = parent.name)
            .add()

            .appendInherited(new KeyedCodec<>("Slot", AttachmentsRegistry.CosmeticSlot.codec()), (data, value) -> data.slot = value, (data) -> data.slot, (data, parent) -> data.slot = parent.slot)
            .add()

            .appendInherited(new KeyedCodec<>("Model", Codec.STRING), (data, value) -> data.model = value, (data) -> data.model, (data, parent) -> data.model = parent.model)
            .addValidator(CommonAssetValidator.MODEL_CHARACTER_ATTACHMENT)
            .metadata(new UIEditorSectionStart("Rendering"))
            .metadata(new UIRebuildCaches(false, UIRebuildCaches.ClientCache.MODELS))
            .metadata(new UIPropertyTitle("Cosmetic Model")).documentation("The model used for rendering this cosmetic.")
            .add()

            .appendInherited(new KeyedCodec<>("Texture", Codec.STRING), (data, value) -> data.texture = value, (data) -> data.texture, (data, parent) -> data.texture = parent.texture)
            .addValidator(CommonAssetValidator.TEXTURE_CHARACTER_ATTACHMENT)
            .metadata(new UIRebuildCaches(UIRebuildCaches.ClientCache.MODELS))
            .metadata(new UIPropertyTitle("Cosmetic Texture"))
            .documentation("The texture used for rendering this cosmetic.")
            .add()

            .appendInherited(new KeyedCodec<>("Alternatives", AttachmentsRegistry.Alternative.CODEC), (data, value) -> data.alternatives = value, (data) -> data.alternatives, (data, parent) -> data.alternatives = parent.alternatives)
            .metadata(new UIEditorSectionStart("Extra"))
            .add()

            .appendInherited(new KeyedCodec<>("SlotOverrides", Codec.STRING_ARRAY), (data, value) -> data.slotOverrides = Arrays.stream(value).map(AttachmentsRegistry.CosmeticSlot::valueOf).toList(), (data) -> data.slotOverrides == null ? null : data.slotOverrides.stream().map(AttachmentsRegistry.CosmeticSlot::name).toList().toArray(new String[0]), (data, parent) -> data.slotOverrides = parent.slotOverrides).add()
            .appendInherited(new KeyedCodec<>("DefaultColor", AttachmentsRegistry.Colour.CODEC), (data, value) -> data.defaultColor = value, (data) -> data.defaultColor, (data, parent) -> data.defaultColor = parent.defaultColor).add()
            .build();

    private AssetIconProperties iconProperties;

    public CustomCosmetic() {
    }

    public CustomCosmetic(String id, String name, String model, String texture, String icon, Map<String, AttachmentsRegistry.Variant> variants, String gradientSet, List<AttachmentsRegistry.CosmeticSlot> slotOverrides, AttachmentsRegistry.Colour defaultColor) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.texture = texture;
        this.icon = icon;
        this.slotOverrides = slotOverrides;
        this.defaultColor = defaultColor;

        this.alternatives = new AttachmentsRegistry.Alternative();

        if (variants != null && !variants.isEmpty()) {
            this.alternatives.variants = variants;
            return;
        }

        if (gradientSet != null && !gradientSet.isEmpty()) {
            this.alternatives.gradientSet = gradientSet;
        }
    }


    public String name() {
        return name;
    }

    public String model() {
        return model;
    }

    public String texture() {
        return texture;
    }

    public String icon() {
        return icon;
    }

    public AttachmentsRegistry.CosmeticSlot slot() {
        return slot;
    }

    public Map<String, AttachmentsRegistry.Variant> variants() {
        return alternatives != null && alternatives.variants != null ? alternatives.variants : Map.of();
    }

    public List<AttachmentsRegistry.CosmeticSlot> slotOverrides() {
        return slotOverrides != null ? slotOverrides : List.of();
    }

    public String gradientSet() {
        return alternatives != null && alternatives.gradientSet != null ? alternatives.gradientSet : "";
    }

    public AttachmentsRegistry.Colour defaultColor() {
        return defaultColor;
    }

    ModelAttachment makeModel(String variant, AttachmentsRegistry.Colour colour) {
        if (variant == null || variant.isEmpty()) {
            return new ModelAttachment(
                    model(),
                    texture(),
                    colour.getGradientSet(),
                    colour.getGradientID(),
                    1
            );
        }

        AttachmentsRegistry.Variant v = variants().get(variant);

        return new ModelAttachment(
                model(),
                v.texture,
                colour.getGradientSet(),
                colour.getGradientID(),
                1
        );
    }

    @Override
    public String getId() {
        return id;
    }

    private static AssetStore<String, CustomCosmetic, DefaultAssetMap<String, CustomCosmetic>> ASSET_STORE;

    public static AssetStore<String, CustomCosmetic, DefaultAssetMap<String, CustomCosmetic>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(CustomCosmetic.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, CustomCosmetic> getAssetMap() {
        return CustomCosmetic.getAssetStore().getAssetMap();
    }
}
