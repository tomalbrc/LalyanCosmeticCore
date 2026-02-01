package com.goodwitchlalya.lalyan_cosmetic_core.util;

import com.goodwitchlalya.lalyan_cosmetic_core.component.CosmeticComponent;
import com.goodwitchlalya.lalyan_cosmetic_core.component.EquippedCosmetic;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAttachment;
import com.hypixel.hytale.server.core.cosmetics.CosmeticRegistry;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

// A singleton registry that manages all custom cosmetic and character attachments.
// It handles loading, storing, and applying these attachments to player models.
public class AttachmentsRegistry {
    private static AttachmentsRegistry INSTANCE;

    // A list of slots that doesn't make the override by default
    public final List<CosmeticSlot> nonOverridingSlots = List.of(CosmeticSlot.Hair_Extension);

    private final List<SlotConnection> connections = List.of(new SlotConnection(CosmeticSlot.Hair_Extension, CosmeticSlot.Haircuts, "Hair", (skin) -> skin.haircut.split("\\.")[1]), new SlotConnection(CosmeticSlot.Mouths, null, "Skin", (skin) -> skin.bodyCharacteristic.split("\\.")[1]));

    // Enum for top-level UI categories.
    public enum TopLevelTypes {Head, General, Torso, Legs, Capes, All}

    // Enum to differentiate between character parts and wearable cosmetics.
    public enum SlotType {CHARACTER, COSMETIC}

    // Enum representing slots for wearable cosmetics like hats and capes.
    public enum CosmeticSlot {
        Capes(SlotType.COSMETIC),
        Face_Accessories(SlotType.COSMETIC),
        Gloves(SlotType.COSMETIC),
        Head(SlotType.COSMETIC),
        Ears_Accessories(SlotType.COSMETIC),
        Overpants(SlotType.COSMETIC),
        Overtops(SlotType.COSMETIC),
        Pants(SlotType.COSMETIC),
        Shoes(SlotType.COSMETIC),
        Undertops(SlotType.COSMETIC),
        Underwears(SlotType.COSMETIC),

        Beards(SlotType.CHARACTER),
        Ears(SlotType.CHARACTER),
        Eyebrows(SlotType.CHARACTER),
        Eyes(SlotType.CHARACTER),
        Faces(SlotType.CHARACTER),
        Mouths(SlotType.CHARACTER),
        Haircuts(SlotType.CHARACTER),
        Hair_Extension(SlotType.CHARACTER),
        Wings(SlotType.CHARACTER),
        Tails(SlotType.CHARACTER),
        Horns(SlotType.CHARACTER),
        Face_Details(SlotType.CHARACTER);

        final SlotType slotType;

        CosmeticSlot(SlotType slotType) {
            this.slotType = slotType;
        }

        public SlotType getType() {
            return slotType;
        }

        public static Codec<CosmeticSlot> codec() {
            var c = new EnumCodec<>(AttachmentsRegistry.CosmeticSlot.class);
            for (CosmeticSlot value : CosmeticSlot.values()) {
                c.documentKey(value, "Slot-type: " + value.slotType.name());
            }
            return c;
        }
    }

    public record GradientSet(String name, List<String> colourList) {
        public GradientSet(String name) {
            this(name, new ArrayList<>());
        }

        public boolean add(String colour) {
            return colourList.add(colour);
        }

        public boolean remove(String colour) {
            return colourList.remove(colour);
        }

        public boolean containsColour(String colour) {
            return colourList.contains(colour);
        }

        @Override
        public String toString() {
            return String.format("%s: [%s]", name, colourList);
        }

    }

    public static class ColoursDataSet {

        private final List<GradientSet> gradientSets;

        public ColoursDataSet(List<GradientSet> gradientSets) {
            this.gradientSets = gradientSets;
        }

        public ColoursDataSet() {
            this.gradientSets = new ArrayList<>();
        }

        public boolean add(GradientSet gradientSet) {
            return gradientSets.add(gradientSet);
        }

        public boolean remove(GradientSet gradientSet) {
            return gradientSets.remove(gradientSet);
        }

        public boolean contains(GradientSet gradientSet) {
            return gradientSets.contains(gradientSet);
        }

        public boolean contains(String gradientSet) {
            return gradientSets.stream().anyMatch((set) -> set.name.equals(gradientSet));
        }

        public GradientSet getGradientSet(String gradientSetName) {
            return gradientSets.stream().filter((set) -> set.name.equals(gradientSetName)).findFirst().orElse(null);
        }

    }

    public static ColoursDataSet coloursDataSet = new ColoursDataSet();

    private static final GradientSet coloredCottonSet = new GradientSet("Colored_Cotton");

    public enum coloredCottonSetNames {
        Black, Blue, Brown, Charcoal, Cream, Green, Grey, Lime, Orange, Pink, Purple, Red, Turquoise, White, Yellow
    }

    private static final GradientSet eyesGradientSet = new GradientSet("Eyes_Gradient");

    public enum eyesGradientSetNames {
        Black, Blond, Blue, BlueLight, Brown, BrownDark, BrownLight, Green, GreenLight, Grey, Honey, Orange, Pink, Purple, Red, RedDark, Turquoise, White
    }

    private static final GradientSet fadedLeatherSet = new GradientSet("Faded_Leather");

    public enum fadedLeatherSetNames {
        Black, Blue, BlueDark, Brown, BrownDark, Green, Grey, Lime, Orange, Orange_Tan, Pink, Purple, Red, Turquoise, Violet, White, Yellow
    }

    private static final GradientSet fantasyCottonSet = new GradientSet("Fantasy_Cotton");

    public enum fantasyCottonSetNames {
        Beige, Black, Blue, Brown, Green, Lime, Orange, Pink, Purple, Red, Turquoise, Yellow
    }

    private static final GradientSet fantasyCottonDarkSet = new GradientSet("Fantasy_Cotton_Dark");

    public enum fantasyCottonDarkSetNames {
        Black, Blue, BlueDark, Brown, Green, Lime, Orange, Pink, Purple, Red, Turquoise, Yellow
    }

    private static final GradientSet flashySyntheticSet = new GradientSet("Flashy_Synthetic");

    public enum flashySyntheticSetNames {
        Black, Blue, Green, Grey, Orange, OrangePastel, Pink, PinkPastel, Purple, Red, Turquoise, Violet, White, Yellow
    }

    private static final GradientSet hairSet = new GradientSet("Hair");

    public enum hairSetNames {
        Black, Blond, BlondCaramel, BlondPlatinum, BlondSand, Blue, Blue_Anthracite, BlueDark, BlueLight, Brown, BrownDark, BrownDarker, BrownLight, BrownSemiDark, BrownSemiLight, Bubblegum, Copper, Green, Grey, GreyAsh, GreyPurple, Lavender, Pink, PinkBerry, PitchBlack, Purple, Red, RedDark, Turquoise, White
    }

    private static final GradientSet jeanGenericSet = new GradientSet("Jean_Generic");

    public enum jeanGenericSetNames {
        Black, Blue, Blue_Night, BluePastel, GreyBlue, GreyDark, GreyLight, Marine_Blue, Maroon, Turquoise_Dark
    }

    private static final GradientSet ornamentedMetalSet = new GradientSet("Ornamented_Metal");

    public enum ornamentedMetalSetNames {
        Brass_Purple, Copper_Green, Gold_Red, Iron_Black, Silver_Blue
    }

    private static final GradientSet pastelCottonSet = new GradientSet("Pastel_Cotton");

    public enum pastelCottonSetNames {
        Black, Blue, Carmin, Green, Grey, Lime, Orange, Pink, PinkPastel, Purple, PurplePastel, Red, Turquoise, White, Yellow
    }

    private static final GradientSet rottenFabricSet = new GradientSet("Rotten_Fabric");

    public enum rottenFabricSetNames {
        Blue, Brown, Yellow
    }

    private static final GradientSet shinyFabricSet = new GradientSet("Shiny_Fabric");

    public enum shinyFabricSetNames {
        Black, Blue, Brown, Green, Grey, Lime, Orange, Pink, Purple, Red, Turquoise, Violet, White, Yellow
    }

    private static final GradientSet skinSet = new GradientSet("Skin");

    public enum skinSetNames {
        s01, s02, s03, s04, s05, s06, s07, s08, s09, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21, s22, s23, s24, s25, s26, s27, s28, s29, s30, s31, s32, s33, s34, s35, s36, s37, s38, s39, s40, s41, s42, s43, s44, s45, s46, s47, /*s48, s49, s50,
        s51, s52*/;

        public String getName() {
            return this.name().replace("s", "");
        }
    }

    static {
        /* Adding Colored_Cotton colours to the set */
        Arrays.stream(coloredCottonSetNames.values()).forEach(value -> coloredCottonSet.add(value.name()));

        /* Adding Eyes_Gradient colours to the set */
        Arrays.stream(eyesGradientSetNames.values()).forEach(value -> eyesGradientSet.add(value.name()));

        /* Adding Faded_Leather colours to the set */
        Arrays.stream(fadedLeatherSetNames.values()).forEach(value -> fadedLeatherSet.add(value.name()));

        /* Adding Fantasy_Cotton colours to the set */
        Arrays.stream(fantasyCottonSetNames.values()).forEach(value -> fantasyCottonSet.add(value.name()));

        /* Adding Fantasy_Cotton_Dark colours to the set */
        Arrays.stream(fantasyCottonDarkSetNames.values()).forEach(value -> fantasyCottonDarkSet.add(value.name()));

        /* Adding Flashy_Synthetic colours to the set */
        Arrays.stream(flashySyntheticSetNames.values()).forEach(value -> flashySyntheticSet.add(value.name()));

        /* Adding Hair colours to the set */
        Arrays.stream(hairSetNames.values()).forEach(value -> hairSet.add(value.name()));

        /* Adding Jean_Generic colours to the set */
        Arrays.stream(jeanGenericSetNames.values()).forEach(value -> jeanGenericSet.add(value.name()));

        /* Adding Ornamented_Metal colours to the set */
        Arrays.stream(ornamentedMetalSetNames.values()).forEach(value -> ornamentedMetalSet.add(value.name()));

        /* Adding Pastel_Cotton colours to the set */
        Arrays.stream(pastelCottonSetNames.values()).forEach(value -> pastelCottonSet.add(value.name()));

        /* Adding Rotten_Fabric colours to the set */
        Arrays.stream(rottenFabricSetNames.values()).forEach(value -> rottenFabricSet.add(value.name()));

        /* Adding Shiny_Fabric colours to the set */
        Arrays.stream(shinyFabricSetNames.values()).forEach(value -> shinyFabricSet.add(value.name()));

        /* Adding Skin colours to the set */
        Arrays.stream(skinSetNames.values()).forEach(value -> skinSet.add(value.getName()));

        coloursDataSet.add(coloredCottonSet);
        coloursDataSet.add(eyesGradientSet);
        coloursDataSet.add(fadedLeatherSet);
        coloursDataSet.add(fantasyCottonSet);
        coloursDataSet.add(fantasyCottonDarkSet);
        coloursDataSet.add(flashySyntheticSet);
        coloursDataSet.add(hairSet);
        coloursDataSet.add(jeanGenericSet);
        coloursDataSet.add(ornamentedMetalSet);
        coloursDataSet.add(pastelCottonSet);
        coloursDataSet.add(rottenFabricSet);
        coloursDataSet.add(shinyFabricSet);
        coloursDataSet.add(skinSet);
    }

    // Provides access to the singleton instance of the registry.
    public static AttachmentsRegistry get() {
        if (INSTANCE == null) INSTANCE = new AttachmentsRegistry();

        return INSTANCE;
    }

    // Returns the raw map of registered attachments.
    public static CustomCosmetic get(String string) {
        return CustomCosmetic.getAssetMap().getAssetMap().get(string);
    }

    public static Map<String, CustomCosmetic> getAll() {
        return CustomCosmetic.getAssetMap().getAssetMap();
    }

    // A record to hold data for a single cosmetic variant (texture and icon).
    public static class Variant {
        String texture;
        String icon;

        public static final BuilderCodec<Variant> CODEC = BuilderCodec.builder(Variant.class, Variant::new).append(new KeyedCodec<>("Texture", Codec.STRING), (data, value) -> data.texture = value, (data) -> data.texture).add().append(new KeyedCodec<>("Icon", Codec.STRING), (data, value) -> data.icon = value, (data) -> data.icon).add().build();

        public Variant() {

        }

        public String texture() {
            return texture;
        }

        public String icon() {
            return icon;
        }
    }

    public record SlotConnection(CosmeticSlot mainSlot, CosmeticSlot connectedSlot, String gradientSet,
                                 Function<PlayerSkin, String> defaultId) {
    }

    public static class Alternative {
        public String gradientSet;
        public Map<String, Variant> variants;

        public static final BuilderCodec<Alternative> CODEC = BuilderCodec.builder(Alternative.class, Alternative::new).append(new KeyedCodec<>("GradientSet", Codec.STRING), (data, value) -> data.gradientSet = value, (data) -> data.gradientSet).add().append(new KeyedCodec<>("Variants", new MapCodec<>(Variant.CODEC, ConcurrentHashMap::new)), (data, value) -> data.variants = value, (data) -> data.variants).add().build();
    }

    public static class Colour {
        public static final BuilderCodec<Colour> CODEC = BuilderCodec.builder(Colour.class, Colour::new).append(new KeyedCodec<>("GradientSet", Codec.STRING), (s, v) -> s.gradientSet = v, s -> s.gradientSet).add().append(new KeyedCodec<>("GradientID", Codec.STRING), (s, v) -> s.gradientID = v, s -> s.gradientID).add().build();

        private String gradientSet;
        private String gradientID;

        private Colour() {
        }

        public Colour(String gradientSet, String gradientID) {
            this.gradientSet = gradientSet;
            this.gradientID = gradientID;
        }

        public void setGradientSet(String gradientSet) {
            this.gradientSet = gradientSet;
        }

        public void setGradientID(String gradientID) {
            this.gradientID = gradientID;
        }

        public String getGradientSet() {
            return gradientSet;
        }

        public String getGradientID() {
            return gradientID;
        }
    }

    // The core method for rebuilding a player's skin. It combines the player's default skin
    // with the custom cosmetics they have equipped.
    // @param ref A reference to the player entity.
    public void rebuildSkinWithCosmetics(Ref<EntityStore> ref) {
        Store<EntityStore> store = ref.getStore();

        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        Model model = store.getComponent(ref, ModelComponent.getComponentType()).getModel();
        PlayerSkin playerSkin = store.getComponent(ref, PlayerSkinComponent.getComponentType()).getPlayerSkin();

        if (data == null) {
            store.addComponent(ref, CosmeticComponent.getComponentType(), new CosmeticComponent());
            return;
        }

        List<ModelAttachment> attachments = new ArrayList<>();
        Map<CosmeticSlot, Boolean> overrides = new HashMap<>();
        List<EquippedCosmetic> invalid = new ArrayList<>();

        // Iterate through the player's equipped cosmetics.
        for (var equippedCosmetic : data.getCosmetics()) {
            var attachment = AttachmentsRegistry.get(equippedCosmetic.getId());
            if (attachment == null) {
                invalid.add(equippedCosmetic);
                continue;
            }

            var slot = attachment.slot();

            if (slot != null) {
                overrides.put(slot, true);
            }

            // Add the attachment's primary slot and any extra override slots to the override map.
            if (nonOverridingSlots.contains(attachment.slot())) {
                overrides.put(attachment.slot(), false);
            }

            SlotConnection connection = connections.stream().filter(c -> c.mainSlot() == attachment.slot()).findFirst().orElse(null);

            String gradientSet = "";
            String gradientId = "";

            if (connection != null) {
                if (connection.gradientSet == null && connection.connectedSlot == null)
                    throw new IllegalArgumentException("Cannot have a connection with no gradient set nor connected slot!");

                gradientSet = connection.gradientSet();

                CustomCosmetic connectedCosmetic = data.getCosmetic(connection.connectedSlot());
                if (connectedCosmetic != null) { // TODO: STORE IN CUSTOM STRUCTURE for color selection
                    gradientSet = connectedCosmetic.defaultColor().getGradientSet();
                    gradientId = connectedCosmetic.defaultColor().getGradientID();
                } else {
                    gradientId = connection.defaultId.apply(playerSkin);
                }
            } else if (!attachment.gradientSet().isEmpty()) {
                String set = attachment.gradientSet();
                GradientSet gs = coloursDataSet.getGradientSet(set);
                if (gs != null && !gs.colourList().isEmpty()) {
                    gradientSet = set;
                    gradientId = gs.colourList().getFirst();
                }
            }

            // Create the model attachment and add it to the list.
            attachments.add(attachment.makeModel(null, new Colour(gradientSet, gradientId)));
        }

        // Clean up any invalid cosmetics from the player's data.
        for (var inv : invalid) {
            data.removeCosmetic(inv);
        }

        // Restore the base skin, skipping parts that are overridden by custom cosmetics.
        restoreSkinWithOverrides(ref, attachments, overrides);

        // Create a new player model with the combined attachments.
        Model newModel = new Model(player.getDisplayName() + "_CustomModel", model.getScale(), model.getRandomAttachmentIds(), attachments.toArray(new ModelAttachment[0]), model.getBoundingBox(), model.getModel(), model.getTexture(), model.getGradientSet(), model.getGradientId(), model.getEyeHeight(), model.getCrouchOffset(), model.getAnimationSetMap(), model.getCamera(), model.getLight(), model.getParticles(), model.getTrails(), model.getPhysicsValues(), model.getDetailBoxes(), model.getPhobia(), model.getPhobiaModelAssetId());

        // Apply the new model to the player.
        store.replaceComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));
        store.replaceComponent(ref, CosmeticComponent.getComponentType(), data);
    }

    // Re-applies the player's default Hytale skin parts (hair, eyes, etc.) unless they
    // are marked as being overridden by a custom cosmetic.
    private void restoreSkinWithOverrides(Ref<EntityStore> ref, List<ModelAttachment> attachments, Map<CosmeticSlot, Boolean> overrides) {
        CosmeticRegistry registry = CosmeticsModule.get().getRegistry();

        Store<EntityStore> store = ref.getStore();
        PlayerSkin playerSkin = store.getComponent(ref, PlayerSkinComponent.getComponentType()).getPlayerSkin();

        String gradientId = playerSkin.bodyCharacteristic.split("\\.")[1];
        String[] bodyCharacteristicParts = playerSkin.bodyCharacteristic.split("\\.");

        // This large block of code checks each vanilla cosmetic slot. If it's not in the 'overrides' map,
        // it resolves the corresponding attachment from the vanilla registry and adds it to the list.
        var bodyCharacteristic = registry.getBodyCharacteristics().get(bodyCharacteristicParts[0]);
        if (bodyCharacteristic != null) {
            attachments.add(ModelUtils.resolveAttachment(bodyCharacteristic, bodyCharacteristicParts, gradientId));
        }

        if (!overrides.getOrDefault(CosmeticSlot.Beards, false)) {
            if (playerSkin.facialHair != null) {
                String[] facialHairsParts = playerSkin.facialHair.split("\\.");
                var facialHairs = registry.getFacialHairs().get(facialHairsParts[0]);
                if (facialHairs != null) {
                    attachments.add(ModelUtils.resolveAttachment(facialHairs, facialHairsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Ears, false)) {
            if (playerSkin.ears != null) {
                String[] earsParts = playerSkin.ears.split("\\.");
                var ears = registry.getEars().get(earsParts[0]);
                if (ears != null) {
                    attachments.add(ModelUtils.resolveAttachment(ears, earsParts, playerSkin.bodyCharacteristic.split("\\.")[1]));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Eyebrows, false)) {
            if (playerSkin.eyebrows != null) {
                String[] eyebrowsParts = playerSkin.eyebrows.split("\\.");
                var eyebrows = registry.getEyebrows().get(eyebrowsParts[0]);
                if (eyebrows != null) {
                    attachments.add(ModelUtils.resolveAttachment(eyebrows, eyebrowsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Eyes, false)) {
            if (playerSkin.eyes != null) {
                String[] eyesParts = playerSkin.eyes.split("\\.");
                var eyes = registry.getEyes().get(eyesParts[0]);
                if (eyes != null) {
                    attachments.add(ModelUtils.resolveAttachment(eyes, eyesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Faces, false)) {
            if (playerSkin.face != null) {
                String[] faceParts = playerSkin.face.split("\\.");
                var face = registry.getFaces().get(faceParts[0]);
                if (face != null) {
                    attachments.add(ModelUtils.resolveAttachment(face, faceParts, playerSkin.bodyCharacteristic.split("\\.")[1]));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Mouths, false)) {
            if (playerSkin.mouth != null) {
                String[] mouthsParts = playerSkin.mouth.split("\\.");
                var mouths = registry.getMouths().get(mouthsParts[0]);
                if (mouths != null) {
                    attachments.add(ModelUtils.resolveAttachment(mouths, mouthsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Haircuts, false)) {
            if (playerSkin.haircut != null) {
                String[] haircutsParts = playerSkin.haircut.split("\\.");
                var haircuts = registry.getHaircuts().get(haircutsParts[0]);
                if (haircuts != null) {
                    attachments.add(ModelUtils.resolveAttachment(haircuts, haircutsParts, gradientId));
                }
            }
        }

        /* Cosmetics Slots */
        if (!overrides.getOrDefault(CosmeticSlot.Capes, false)) {
            if (playerSkin.cape != null) {
                String[] capesParts = playerSkin.cape.split("\\.");
                var capes = registry.getCapes().get(capesParts[0]);
                if (capes != null) {
                    attachments.add(ModelUtils.resolveAttachment(capes, capesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Face_Accessories, false)) {
            if (playerSkin.faceAccessory != null) {
                String[] faceAccessoriesParts = playerSkin.faceAccessory.split("\\.");
                var faceAccessories = registry.getFaceAccessories().get(faceAccessoriesParts[0]);
                if (faceAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(faceAccessories, faceAccessoriesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Gloves, false)) {
            if (playerSkin.gloves != null) {
                String[] glovesParts = playerSkin.gloves.split("\\.");
                var gloves = registry.getGloves().get(glovesParts[0]);
                if (gloves != null) {
                    attachments.add(ModelUtils.resolveAttachment(gloves, glovesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Head, false)) {
            if (playerSkin.headAccessory != null) {
                String[] headAccessoriesParts = playerSkin.headAccessory.split("\\.");
                var headAccessories = registry.getHeadAccessories().get(headAccessoriesParts[0]);
                if (headAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(headAccessories, headAccessoriesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Overpants, false)) {
            if (playerSkin.overpants != null) {
                String[] overpantsParts = playerSkin.overpants.split("\\.");
                var overpants = registry.getOverpants().get(overpantsParts[0]);
                if (overpants != null) {
                    attachments.add(ModelUtils.resolveAttachment(overpants, overpantsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Overtops, false)) {
            if (playerSkin.overtop != null) {
                String[] overtopsParts = playerSkin.overtop.split("\\.");
                var overtops = registry.getOvertops().get(overtopsParts[0]);
                if (overtops != null) {
                    attachments.add(ModelUtils.resolveAttachment(overtops, overtopsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Pants, false)) {
            if (playerSkin.pants != null) {
                String[] pantsParts = playerSkin.pants.split("\\.");
                var pants = registry.getPants().get(pantsParts[0]);
                if (pants != null) {
                    attachments.add(ModelUtils.resolveAttachment(pants, pantsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Shoes, false)) {
            if (playerSkin.shoes != null) {
                String[] shoesParts = playerSkin.shoes.split("\\.");
                var shoes = registry.getShoes().get(shoesParts[0]);
                if (shoes != null) {
                    attachments.add(ModelUtils.resolveAttachment(shoes, shoesParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Undertops, false)) {
            if (playerSkin.undertop != null) {
                String[] undertopsParts = playerSkin.undertop.split("\\.");
                var undertops = registry.getUndertops().get(undertopsParts[0]);
                if (undertops != null) {
                    attachments.add(ModelUtils.resolveAttachment(undertops, undertopsParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Underwears, false)) {
            if (playerSkin.underwear != null) {
                String[] underwearParts = playerSkin.underwear.split("\\.");
                var underwear = registry.getUnderwear().get(underwearParts[0]);
                if (underwear != null) {
                    attachments.add(ModelUtils.resolveAttachment(underwear, underwearParts, gradientId));
                }
            }
        }

        if (!overrides.getOrDefault(CosmeticSlot.Ears_Accessories, false)) {
            if (playerSkin.earAccessory != null) {
                String[] earAccessoriesParts = playerSkin.earAccessory.split("\\.");
                var earAccessories = registry.getEarAccessories().get(earAccessoriesParts[0]);
                if (earAccessories != null) {
                    attachments.add(ModelUtils.resolveAttachment(earAccessories, earAccessoriesParts, gradientId));
                }
            }
        }

        if (playerSkin.skinFeature != null) {
            String[] skinFeaturesParts = playerSkin.skinFeature.split("\\.");
            var skinFeatures = registry.getSkinFeatures().get(skinFeaturesParts[0]);
            if (skinFeatures != null) {
                attachments.add(ModelUtils.resolveAttachment(skinFeatures, skinFeaturesParts, gradientId));
            }
        }
    }

    // Checks if a player has marked a specific slot as empty.
    public boolean isEmptySlot(Ref<EntityStore> ref, CosmeticSlot slot) {
        return !isEquipped(ref, slot);
    }

    // Checks if a player has a specific cosmetic ID (including variant) equipped.
    public boolean isEquipped(Ref<EntityStore> ref, String cosmeticId) {
        Store<EntityStore> store = ref.getStore();
        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());

        if (data == null) return false;

        if (data.getCosmetics().contains(cosmeticId))
            return true;

        var a = AttachmentsRegistry.get(cosmeticId);
        return a != null && a.variants().containsKey(cosmeticId);
    }

    public boolean isEquipped(Ref<EntityStore> ref, CosmeticSlot slot) {
        return getEquipped(ref, slot) != null;
    }

    public CustomCosmetic getEquipped(Ref<EntityStore> ref, CosmeticSlot slot) {
        Store<EntityStore> store = ref.getStore();
        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());

        if (data == null)
            return null;

        for (var cosmetic : data.getCosmetics()) {
            var current = AttachmentsRegistry.get(cosmetic.getId());
            if (current != null && slot == current.slot) {
                return current;
            }
        }

        return null;
    }

    // Removes a cosmetic (and any of its variants) from a player.
    public void removeCosmetic(Ref<EntityStore> ref, String cosmeticId) {
        removeCosmetic(ref, cosmeticId, false);
    }

    public void removeCosmetic(Ref<EntityStore> ref, @Nonnull String cosmeticId, boolean multiSelect) {
        Store<EntityStore> store = ref.getStore();
        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());

        if (data == null) return;

        CustomCosmetic customCosmetic = CustomCosmetic.getAssetStore().getAssetMap().getAsset(cosmeticId);

        data.getCosmetics().removeIf(x -> x.getId().equals(cosmeticId));

        if (customCosmetic != null && !multiSelect) {
            Set<CosmeticSlot> slotsToCheck = new HashSet<>();
            if (customCosmetic.slot() != null) {
                slotsToCheck.add(customCosmetic.slot());
            }
            slotsToCheck.addAll(customCosmetic.slotOverrides());

            for (var s : slotsToCheck) {
                if (!data.isSlotUsed(s)) {
                    data.removeCosmetic(s);
                }
            }
        }

        store.replaceComponent(ref, CosmeticComponent.getComponentType(), data);

        rebuildSkinWithCosmetics(ref);
    }

    // Adds a cosmetic to a player.
    // @param multiSelect If true, does not clear the slot and ignores slot overrides.
    public void addCosmetic(Ref<EntityStore> ref, String cosmetic, String variant, Colour colour, boolean multiSelect) {
        // Fix for Issue 3: Remove any existing version of this cosmetic (base, variant, or gradient)
        // This ensures we don't retain old colors/variants if we are equipping the base one.
        removeCosmetic(ref, cosmetic, true);

        Store<EntityStore> store = ref.getStore();
        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());

        if (data == null) return;

        CustomCosmetic attachment = CustomCosmetic.getAssetMap().getAsset(cosmetic);
        CosmeticSlot slot = attachment != null ? attachment.slot() : null;

        boolean isNonOverriding = slot != null && nonOverridingSlots.contains(slot);
        boolean shouldClear = !multiSelect && !isNonOverriding;

        if (shouldClear) {
            if (slot != null) {
                clearSlot(ref, slot);
                data.removeCosmetic(slot);
            }
        }

        // Add any necessary slot overrides for this cosmetic.
        if (attachment != null && !multiSelect) {
            for (var overrideSlot : attachment.slotOverrides()) {
                clearSlot(ref, overrideSlot);
                data.removeCosmetic(overrideSlot);
            }
        }

        if (attachment != null) {
            data.addCosmetic(attachment, variant, colour);
        }

        rebuildSkinWithCosmetics(ref);
    }

    // Clears all cosmetics from a given slot, determined by a cosmetic ID.
    public void clearSlot(Ref<EntityStore> ref, String id) {
        CustomCosmetic attachment = CustomCosmetic.getAssetMap().getAssetMap().get(id);

        if (attachment == null) return;

        clearSlot(ref, attachment.slot());
    }

    // Clears all cosmetics from a specific slot.
    public void clearSlot(Ref<EntityStore> ref, CosmeticSlot slot) {
        Store<EntityStore> store = ref.getStore();
        CosmeticComponent data = store.getComponent(ref, CosmeticComponent.getComponentType());

        if (data == null) return;

        data.getCosmetics().removeIf(x -> slot.equals(AttachmentsRegistry.get(x.getId()).slot()));
        rebuildSkinWithCosmetics(ref);
    }

    // Clears all wearable cosmetics from the player.
    public void clearCosmetics(Ref<EntityStore> ref) {
        for (CosmeticSlot slot : CosmeticSlot.values()) {
            clearSlot(ref, slot);
        }
    }

    // Clears all character parts from the player.
    public void clearCharacter(Ref<EntityStore> ref) {
        for (CosmeticSlot slot : CosmeticSlot.values()) {
            if (slot.getType() == SlotType.CHARACTER)
                clearSlot(ref, slot);
        }
    }

    // Clears all custom attachments (both cosmetics and character parts) from the player.
    public void clearAll(Ref<EntityStore> ref) {
        clearCosmetics(ref);
        clearCharacter(ref);
    }


    // Returns a sorted list of all registered cosmetic IDs.
    public List<String> getAttachmentsList() {
        return CustomCosmetic.getAssetMap().getAssetMap().keySet().stream().sorted().collect(Collectors.toList());
    }
}