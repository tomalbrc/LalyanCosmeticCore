package com.goodwitchlalya.lalyan_cosmetic_core.gui.page;

import com.goodwitchlalya.lalyan_cosmetic_core.component.UnlockedCosmeticsComponent;
import com.goodwitchlalya.lalyan_cosmetic_core.util.AttachmentsRegistry;
import com.goodwitchlalya.lalyan_cosmetic_core.util.CustomCosmetic;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.*;

/**
 * Represents the main interactive UI page for cosmetic customization.
 */
public class CosmeticPage extends InteractiveCustomUIPage<CosmeticPage.Data> {

    // --- Constants & Static Data ---
    private static final int ITEMS_PER_ROW = 8;
    private static final Map<String, String> colorCodes = new HashMap<>();

    static {
        colorCodes.put("Beige", "#F5F5DC");
        colorCodes.put("Black", "#1D1D21");
        colorCodes.put("Blond", "#FAE7B5");
        colorCodes.put("BlondCaramel", "#C68E17");
        colorCodes.put("BlondPlatinum", "#F0F0EC");
        colorCodes.put("BlondSand", "#DCD0BA");
        colorCodes.put("Blue", "#3C44AA");
        colorCodes.put("BlueDark", "#212663");
        colorCodes.put("BlueLight", "#8FA5D6");
        colorCodes.put("BluePastel", "#A1B4D6");
        colorCodes.put("Blue_Anthracite", "#2A303C");
        colorCodes.put("Blue_Night", "#161B40");
        colorCodes.put("Brass_Purple", "#B5A642");
        colorCodes.put("Brown", "#633C1F");
        colorCodes.put("BrownDark", "#3B2412");
        colorCodes.put("BrownDarker", "#26170B");
        colorCodes.put("BrownLight", "#8F6B4E");
        colorCodes.put("BrownSemiDark", "#4F3019");
        colorCodes.put("BrownSemiLight", "#785233");
        colorCodes.put("Bubblegum", "#FF85A2");
        colorCodes.put("Carmin", "#960018");
        colorCodes.put("Charcoal", "#36454F");
        colorCodes.put("Copper", "#B87333");
        colorCodes.put("Copper_Green", "#4CB7A5");
        colorCodes.put("Cream", "#FFFDD0");
        colorCodes.put("Gold_Red", "#D4AF37");
        colorCodes.put("Green", "#5E7C16");
        colorCodes.put("GreenLight", "#83AA2E");
        colorCodes.put("Grey", "#808080");
        colorCodes.put("GreyAsh", "#B2BEB5");
        colorCodes.put("GreyBlue", "#778899");
        colorCodes.put("GreyDark", "#404040");
        colorCodes.put("GreyLight", "#D3D3D3");
        colorCodes.put("GreyPurple", "#7D7096");
        colorCodes.put("Honey", "#D4AF37");
        colorCodes.put("Iron_Black", "#434B4D");
        colorCodes.put("Lavender", "#E6E6FA");
        colorCodes.put("Lime", "#76C610");
        colorCodes.put("Marine_Blue", "#000080");
        colorCodes.put("Maroon", "#800000");
        colorCodes.put("Orange", "#F07613");
        colorCodes.put("OrangePastel", "#FFB347");
        colorCodes.put("Orange_Tan", "#D2B48C");
        colorCodes.put("Pink", "#F28CA7");
        colorCodes.put("PinkBerry", "#990F4B");
        colorCodes.put("PinkPastel", "#FFD1DC");
        colorCodes.put("PitchBlack", "#050505");
        colorCodes.put("Purple", "#800080");
        colorCodes.put("PurplePastel", "#C3B1E1");
        colorCodes.put("Red", "#B02E26");
        colorCodes.put("RedDark", "#5E1814");
        colorCodes.put("Silver_Blue", "#B0C4DE");
        colorCodes.put("Turquoise", "#40E0D0");
        colorCodes.put("Turquoise_Dark", "#00CED1");
        colorCodes.put("Violet", "#EE82EE");
        colorCodes.put("White", "#FFFFFF");
        colorCodes.put("Yellow", "#FED83D");
        colorCodes.put("01", "#190F0D");
        colorCodes.put("02", "#31221F");
        colorCodes.put("03", "#4D272B");
        colorCodes.put("04", "#4F2A24");
        colorCodes.put("05", "#513425");
        colorCodes.put("06", "#5E3A2F");
        colorCodes.put("07", "#765E48");
        colorCodes.put("08", "#63492F");
        colorCodes.put("09", "#6F3B2C");
        colorCodes.put("10", "#6C3F40");
        colorCodes.put("11", "#7D432B");
        colorCodes.put("12", "#945D44");
        colorCodes.put("13", "#AB7A4C");
        colorCodes.put("14", "#BA7F5B");
        colorCodes.put("15", "#D98C5B");
        colorCodes.put("16", "#D5A082");
        colorCodes.put("17", "#E0AE72");
        colorCodes.put("18", "#DCC5B0");
        colorCodes.put("19", "#DCC7A8");
        colorCodes.put("20", "#F4C39A");
        colorCodes.put("21", "#F5C490");
        colorCodes.put("22", "#F5BC83");
        colorCodes.put("23", "#B22A2A");
        colorCodes.put("24", "#F06F47");
        colorCodes.put("25", "#FC8572");
        colorCodes.put("26", "#FF9C5B");
        colorCodes.put("27", "#F4C944");
        colorCodes.put("28", "#B0B283");
        colorCodes.put("29", "#D5F0A0");
        colorCodes.put("30", "#9BC55D");
        colorCodes.put("31", "#5EAE37");
        colorCodes.put("32", "#50843A");
        colorCodes.put("33", "#A0DFFF");
        colorCodes.put("34", "#8AACFB");
        colorCodes.put("35", "#3276C3");
        colorCodes.put("36", "#4354E6");
        colorCodes.put("37", "#263D50");
        colorCodes.put("38", "#6C2ABD");
        colorCodes.put("39", "#A78AF1");
        colorCodes.put("40", "#DDBFE8");
        colorCodes.put("41", "#EC6FF7");
        colorCodes.put("42", "#FF72C2");
        colorCodes.put("43", "#FF95CD");
        colorCodes.put("44", "#F0B9F2");
        colorCodes.put("45", "#F3F3F3");
        colorCodes.put("46", "#2B2B2F");
        colorCodes.put("47", "#131111");
    }

    // --- Fields ---
    private final boolean creative;
    private final UnlockedCosmeticsComponent owned;

    // Selection State
    private AttachmentsRegistry.CosmeticSlot currentSlot = AttachmentsRegistry.CosmeticSlot.Haircuts;
    private AttachmentsRegistry.TopLevelTypes tlt = AttachmentsRegistry.TopLevelTypes.Head;
    private boolean singleSelect = true;

    // Filter & Data State
    private String search;
    private String selectedId;
    private String originalId;
    private String gradientSet;
    private Map<String, AttachmentsRegistry.Variant> variants = new HashMap<>();

    public CosmeticPage(@NonNullDecl PlayerRef playerRef, UnlockedCosmeticsComponent owned, boolean creative) {
        super(playerRef, CustomPageLifetime.CanDismiss, Data.CODEC);
        this.owned = owned;
        this.creative = creative;
    }

    // =================================================================================
    // UI BUILDING
    // =================================================================================

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder cmd, @NonNullDecl UIEventBuilder evt, @NonNullDecl Store<EntityStore> store) {
        cmd.append("Pages/CosmeticGUI/LCC_CosmeticPage.ui");

        if (this.search != null) {
            cmd.set("#SearchInput.Value", this.search);
        }

        buildTopLevelButtons(cmd, evt);
        buildCosmeticGrid(ref, cmd, evt);
        buildSelectionMenu(cmd, evt);

        // Update MultiSelect Checkbox
        cmd.set("#Title #MultiSelect #CheckBox.Value", !this.singleSelect);
        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#Title #MultiSelect #CheckBox", EventData.of("@MultiSelect", "#Title #MultiSelect #CheckBox.Value"));

        updateCamera();
    }

    private void buildSelectionMenu(UICommandBuilder cmd, UIEventBuilder evt) {
        if (selectedId != null && !selectedId.isBlank()) {
            var cosmetic = AttachmentsRegistry.get(selectedId);
            if (cosmetic != null) {
                cmd.set("#WithdrawSelectionLabel.Text", cosmetic.name());
                cmd.set("#WithdrawSelectionButton.Disabled", false);
                cmd.set("#WithdrawIcon.Visible", true);
                cmd.set("#WithdrawIcon.AssetPath", cosmetic.icon());

                evt.addEventBinding(CustomUIEventBindingType.Activating, "#WithdrawSelectionButton", EventData.of("Action", "withdraw"));
                return;
            }
        }

        cmd.set("#WithdrawIcon.Visible", false);
        cmd.set("#WithdrawSelectionLabel.Text", "");
        cmd.set("#WithdrawSelectionButton.Disabled", true);
    }

    private void buildTopLevelButtons(UICommandBuilder cmd, UIEventBuilder evt) {
        for (AttachmentsRegistry.TopLevelTypes type : AttachmentsRegistry.TopLevelTypes.values()) {
            String selector = "#TL" + type.name();
            cmd.append("#LLSidePanel #Content " + selector, "Pages/CosmeticGUI/TLButtons/" + type.name() + ".ui");
            evt.addEventBinding(CustomUIEventBindingType.Activating, "#LLSidePanel #Content " + selector + " #CategoryButton", EventData.of("TLT", type.name()));

            boolean isSelected = (this.tlt == type);
            cmd.set("#LLSidePanel #Content " + selector + " #CategoryButton.Visible", !isSelected);
            cmd.set("#LLSidePanel #Content " + selector + " #CategoryButtonEnabled.Visible", isSelected);

            if (isSelected) {
                cmd.append("#LSidePanel #Content #CategoryButton", "Pages/CosmeticGUI/Categories/" + type.name() + ".ui");
                buildSubCategories(type, cmd, evt);
            }
        }
    }

    private void buildSubCategories(AttachmentsRegistry.TopLevelTypes type, UICommandBuilder cmd, UIEventBuilder evt) {
        switch (type) {
            case Head -> {
                setupCategoryButton(cmd, evt, "Haircut", AttachmentsRegistry.CosmeticSlot.Haircuts);
                setupCategoryButton(cmd, evt, "HairExtension", AttachmentsRegistry.CosmeticSlot.Hair_Extension);
                setupCategoryButton(cmd, evt, "Eyebrows", AttachmentsRegistry.CosmeticSlot.Eyebrows);
                setupCategoryButton(cmd, evt, "Eyes", AttachmentsRegistry.CosmeticSlot.Eyes);
                setupCategoryButton(cmd, evt, "FacialHair", AttachmentsRegistry.CosmeticSlot.Beards);
                setupCategoryButton(cmd, evt, "HeadAccessories", AttachmentsRegistry.CosmeticSlot.Head);
                setupCategoryButton(cmd, evt, "FaceAccessories", AttachmentsRegistry.CosmeticSlot.Face_Accessories);
                setupCategoryButton(cmd, evt, "EarAccessories", AttachmentsRegistry.CosmeticSlot.Ears_Accessories);
                setupCategoryButton(cmd, evt, "Horns", AttachmentsRegistry.CosmeticSlot.Horns);
            }
            case General -> {
                setupCategoryButton(cmd, evt, "Underwear", AttachmentsRegistry.CosmeticSlot.Underwears);
                setupCategoryButton(cmd, evt, "Face", AttachmentsRegistry.CosmeticSlot.Faces);
                setupCategoryButton(cmd, evt, "FaceDetails", AttachmentsRegistry.CosmeticSlot.Face_Details);
                setupCategoryButton(cmd, evt, "Mouth", AttachmentsRegistry.CosmeticSlot.Mouths);
                setupCategoryButton(cmd, evt, "Ears", AttachmentsRegistry.CosmeticSlot.Ears);
            }
            case Torso -> {
                setupCategoryButton(cmd, evt, "Undertops", AttachmentsRegistry.CosmeticSlot.Undertops);
                setupCategoryButton(cmd, evt, "Overtops", AttachmentsRegistry.CosmeticSlot.Overtops);
                setupCategoryButton(cmd, evt, "Gloves", AttachmentsRegistry.CosmeticSlot.Gloves);
                setupCategoryButton(cmd, evt, "Tails", AttachmentsRegistry.CosmeticSlot.Tails);
                setupCategoryButton(cmd, evt, "Wings", AttachmentsRegistry.CosmeticSlot.Wings);
            }
            case Legs -> {
                setupCategoryButton(cmd, evt, "Pants", AttachmentsRegistry.CosmeticSlot.Pants);
                setupCategoryButton(cmd, evt, "Overpants", AttachmentsRegistry.CosmeticSlot.Overpants);
                setupCategoryButton(cmd, evt, "Shoes", AttachmentsRegistry.CosmeticSlot.Shoes);
            }
            case Capes -> setupCategoryButton(cmd, evt, "Capes", AttachmentsRegistry.CosmeticSlot.Capes);
            case All -> setupCategoryButton(cmd, evt, "All", null);
        }
    }

    private void setupCategoryButton(UICommandBuilder cmd, UIEventBuilder evt, String groupName, AttachmentsRegistry.CosmeticSlot slot) {
        boolean isSelected = (this.currentSlot == slot);
        String path = "#LSidePanel #Content #CategoryButton #" + groupName;

        cmd.set(path + " #CategoryButton.Visible", !isSelected);
        cmd.set(path + " #CategoryButtonEnabled.Visible", isSelected);
        evt.addEventBinding(CustomUIEventBindingType.Activating, path + " #CategoryButton", EventData.of("Slot", slot != null ? slot.name() : "All"));
    }

    private void buildCosmeticGrid(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt) {
        cmd.clear("#Content #CosmeticGrid");
        cmd.appendInline("#Content #CosmeticGrid", "Group #CosmeticRow { LayoutMode: Top; }");

        List<CustomCosmetic> entries = getFilteredCosmetics();
        boolean showVanish = currentSlot != null && currentSlot != AttachmentsRegistry.CosmeticSlot.Faces;
        int totalItems = entries.size() + (showVanish ? 1 : 0);

        for (int i = 0; i < totalItems; i++) {
            int rowIndex = i / ITEMS_PER_ROW;
            int colIndex = i % ITEMS_PER_ROW;

            if (colIndex == 0) {
                cmd.append("#CosmeticRow", "Pages/CosmeticGUI/CosmeticRow.ui");
            }

            String rowSelector = "#CosmeticRow[" + rowIndex + "]";
            cmd.append(rowSelector + " #CosmeticSlot", "Pages/CosmeticGUI/CosmeticSlot.ui");
            String slotSelector = rowSelector + " #CosmeticSlot[" + colIndex + "]";

            // Handle Vanish Button (First slot if enabled)
            if (showVanish && i == 0) {
                buildVanishButton(ref, cmd, evt, slotSelector);
                continue;
            }

            // Handle Regular Item
            int entryIndex = showVanish ? i - 1 : i;
            CustomCosmetic entry = entries.get(entryIndex);
            buildCosmeticItem(ref, cmd, evt, slotSelector, entry);
        }

        evt.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput", EventData.of("@Search", "#SearchInput.Value"), false);

        // Build Side Panels (Variants/Colors)
        if (!buildVariantsPanel(ref, cmd, evt)) {
            buildColorPanel(ref, cmd, evt);
        }
    }

    private List<CustomCosmetic> getFilteredCosmetics() {
        return AttachmentsRegistry.getAll().values().stream()
                .filter(a -> tlt == AttachmentsRegistry.TopLevelTypes.All || a.slot() == currentSlot)
                .filter(a -> search == null || search.isEmpty() || a.name().toLowerCase().contains(search.toLowerCase()))
                .filter(a -> creative || owned.contains(a.getId()))
                .sorted(Comparator.comparing(CustomCosmetic::name))
                .toList();
    }

    private void buildVanishButton(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt, String selector) {
        cmd.set(selector + " #Icon.AssetPath", "UI/Custom/Common/Categories/VanishPart.png");
        boolean isEmpty = AttachmentsRegistry.get().isEmptySlot(ref, currentSlot);

        cmd.set(selector + " #Button.Visible", !isEmpty);
        cmd.set(selector + " #ButtonEnabled.Visible", isEmpty);
        cmd.set(selector + " #VariantIcon.Visible", false);

        evt.addEventBinding(CustomUIEventBindingType.Activating, selector + " #Button", EventData.of("CosmeticId", "").append("Enabled", "false"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, selector + " #ButtonEnabled", EventData.of("CosmeticId", "").append("Enabled", "true"));
    }

    private void buildCosmeticItem(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt, String selector, CustomCosmetic entry) {
        String cosmeticId = entry.getId();
        if (entry.icon() != null) cmd.set(selector + " #Icon.AssetPath", entry.icon());
        cmd.set(selector + " #Button.TooltipText", entry.name());
        cmd.set(selector + " #ButtonEnabled.TooltipText", entry.name());

        boolean isEquipped = AttachmentsRegistry.get().isEquipped(ref, cosmeticId);
        cmd.set(selector + " #Button.Visible", !isEquipped);
        cmd.set(selector + " #ButtonEnabled.Visible", isEquipped);

        evt.addEventBinding(CustomUIEventBindingType.Activating, selector + " #Button", EventData.of("CosmeticId", cosmeticId).append("Enabled", "false"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, selector + " #ButtonEnabled", EventData.of("CosmeticId", cosmeticId).append("Enabled", "true"));

        // Variants / Gradients
        if (entry.gradientSet() != null && !entry.gradientSet().isEmpty()) {
            cmd.set(selector + " #VariantIcon.Visible", true);
            String gradParam = entry.name() + "%" + entry.gradientSet();
            evt.addEventBinding(CustomUIEventBindingType.RightClicking, selector + " #Button", EventData.of("GradientId", gradParam));
            evt.addEventBinding(CustomUIEventBindingType.RightClicking, selector + " #ButtonEnabled", EventData.of("GradientId", gradParam));
        } else {
            boolean hasVariants = entry.variants() != null && !entry.variants().isEmpty();
            cmd.set(selector + " #VariantIcon.Visible", hasVariants);
            if (hasVariants) {
                evt.addEventBinding(CustomUIEventBindingType.RightClicking, selector + " #Button", EventData.of("VariantId", cosmeticId));
                evt.addEventBinding(CustomUIEventBindingType.RightClicking, selector + " #ButtonEnabled", EventData.of("VariantId", cosmeticId));
            }
        }
    }

    private boolean buildVariantsPanel(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt) {
        if (variants == null || variants.isEmpty()) {
            cmd.set("#RSidePanel.Visible", false);
            return false;
        }

        cmd.clear("#RSidePanel #Content #VariantList #VariantSlot");
        cmd.set("#RSidePanel.Visible", true);

        // Default Variant
        addVariantSlot(ref, cmd, evt, 0, AttachmentsRegistry.get(originalId).icon(), "Default", originalId);

        // Other Variants
        int index = 1;
        for (Map.Entry<String, AttachmentsRegistry.Variant> entry : variants.entrySet()) {
            String fullId = originalId + "$" + entry.getKey();
            addVariantSlot(ref, cmd, evt, index++, entry.getValue().icon(), entry.getKey(), fullId);
        }
        return true;
    }

    private void addVariantSlot(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt, int index, String icon, String tooltip, String id) {
        String selector = "#VariantSlot[" + index + "]";
        cmd.append("#RSidePanel #Content #VariantList #VariantSlot", "Pages/CosmeticGUI/VariantSlot.ui");

        cmd.set("#RSidePanel #Content #VariantList " + selector + " #Icon.AssetPath", icon);
        cmd.set("#RSidePanel #Content #VariantList " + selector + " #Button.TooltipText", tooltip);
        cmd.set("#RSidePanel #Content #VariantList " + selector + " #ButtonEnabled.TooltipText", tooltip);

        boolean equipped = AttachmentsRegistry.get().isEquipped(ref, id);
        cmd.set("#RSidePanel #Content #VariantList " + selector + " #Button.Visible", !equipped);
        cmd.set("#RSidePanel #Content #VariantList " + selector + " #ButtonEnabled.Visible", equipped);

        evt.addEventBinding(CustomUIEventBindingType.Activating, "#RSidePanel #Content #VariantList " + selector + " #Button", EventData.of("CosmeticId", id).append("Enabled", "false"));
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#RSidePanel #Content #VariantList " + selector + " #ButtonEnabled", EventData.of("CosmeticId", id).append("Enabled", "true"));
    }

    private void buildColorPanel(Ref<EntityStore> ref, UICommandBuilder cmd, UIEventBuilder evt) {
        if (gradientSet == null || gradientSet.isEmpty()) return;

        cmd.set("#RSidePanel.Visible", true);
        String setName = gradientSet.split("%")[1];
        AttachmentsRegistry.GradientSet set = AttachmentsRegistry.coloursDataSet.getGradientSet(setName);

        List<String> colors = set.colourList();
        for (int i = 0; i < colors.size(); i++) {
            String color = colors.get(i);
            String selector = "#VariantSlot[" + i + "]";

            cmd.append("#RSidePanel #Content #VariantList #VariantSlot", "Pages/CosmeticGUI/ColorSlot.ui");

            // NOTE: colorCodes map must be populated for this to work
            String hexCode = colorCodes.getOrDefault(color, "#FFFFFF");
            cmd.set("#RSidePanel #Content #VariantList " + selector + " #Button #Colors.Background", hexCode);
            cmd.set("#RSidePanel #Content #VariantList " + selector + " #Button.TooltipText", color);

            String fullId = originalId + ":" + color;
            boolean equipped = AttachmentsRegistry.get().isEquipped(ref, fullId);
            cmd.set("#RSidePanel #Content #VariantList " + selector + " #Button #SelectedHighlight.Visible", equipped);

            evt.addEventBinding(CustomUIEventBindingType.Activating, "#RSidePanel #Content #VariantList " + selector + " #Button", EventData.of("CosmeticId", fullId).append("Enabled", String.valueOf(equipped)));
        }
    }

    private void updateCamera() {
        Vector3f headRot = this.playerRef.getHeadRotation();
        ServerCameraSettings settings = new ServerCameraSettings();

        settings.distance = 2;
        settings.positionLerpSpeed = 0.1f;
        settings.rotationLerpSpeed = 0.1f;
        settings.displayCursor = true;
        settings.isFirstPerson = false;
        settings.mouseInputTargetType = MouseInputTargetType.None;
        settings.sendMouseMotion = false;
        settings.mouseInputType = MouseInputType.LookAtPlane;
        settings.rotationType = RotationType.Custom;
        settings.eyeOffset = true;
        settings.allowPitchControls = false;
        settings.planeNormal = new com.hypixel.hytale.protocol.Vector3f(0, 0, 0);
        settings.rotation = new Direction((float) (headRot.getYaw() + Math.PI), headRot.getPitch(), headRot.getRoll());
        settings.positionOffset = new Position(0, -0.3, 0);

        if (currentSlot != null) {
            applyCameraOffset(settings, headRot);
        }

        this.playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, true, settings));
    }

    private void applyCameraOffset(ServerCameraSettings settings, Vector3f headRot) {
        switch (currentSlot) {
            case Haircuts, Hair_Extension -> {
                settings.positionOffset = new Position(0, 0.2, 0);
                settings.rotation = new Direction(headRot.getYaw(), headRot.getPitch(), headRot.getRoll());
            }
            case Eyebrows, Eyes, Beards, Faces, Mouths, Face_Details, Face_Accessories -> {
                settings.positionOffset = new Position(0, 0, 0);
                settings.distance = 1;
            }
            case Horns, Head -> settings.positionOffset = new Position(0, 0.1, 0);
            case Underwears, Overpants, Shoes, Pants -> {
                settings.positionOffset = new Position(0, -1.2, 0);
                settings.distance = 1;
            }
            case Undertops, Overtops -> {
                settings.positionOffset = new Position(0, -0.5, 0);
                settings.distance = 1;
            }
            case Tails, Wings, Capes -> settings.rotation = new Direction(headRot.getYaw(), headRot.getPitch(), headRot.getRoll());
        }
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl Data data) {
        super.handleDataEvent(ref, store, data);

        if ("withdraw".equals(data.action)) {
            handleWithdraw(ref, store);
            refreshUI();
            return;
        }

        this.singleSelect = !data.multiSelect;

        if (data.search != null) {
            this.search = data.search;
            UICommandBuilder cmd = new UICommandBuilder();
            UIEventBuilder evt = new UIEventBuilder();
            buildCosmeticGrid(ref, cmd, evt); // Only rebuild grid
            this.sendUpdate(cmd, evt, false);
            return;
        }

        if (data.variantId != null) {
            setVariantMode(data.variantId);
            return;
        }

        if (data.gradientId != null) {
            setGradientMode(data.gradientId);
            return;
        }

        if (data.tlt != null) {
            setTopLevelCategory(data.tlt);
            return;
        }

        if (data.slot != null) {
            setSubCategory(data.slot);
            return;
        }

        handleEquipToggle(ref, data);
    }

    private void handleWithdraw(Ref<EntityStore> ref, Store<EntityStore> store) {
        var comp = store.ensureAndGetComponent(ref, UnlockedCosmeticsComponent.getComponentType());
        var asset = CustomCosmetic.getAssetMap().getAsset(selectedId);
        if (asset != null && (creative || comp.contains(selectedId))) {
            var didRemove = comp.removeCosmetic(selectedId);
            if (didRemove) {
                store.replaceComponent(ref, UnlockedCosmeticsComponent.getComponentType(), comp);
                ItemUtils.dropItem(ref, new ItemStack(asset.getItemId(), 1), ref.getStore());

                AttachmentsRegistry.get().removeCosmetic(ref, selectedId, !singleSelect);
            }
        }
    }

    private void setVariantMode(String variantId) {
        this.gradientSet = null;
        this.variants = new HashMap<>();
        CustomCosmetic c = AttachmentsRegistry.get(variantId);
        if (c != null) this.variants = c.variants();
        this.originalId = variantId;
        refreshUI();
    }

    private void setGradientMode(String gradientId) {
        this.gradientSet = gradientId;
        this.originalId = gradientId;
        refreshUI();
    }

    private void setTopLevelCategory(AttachmentsRegistry.TopLevelTypes tltName) {
        resetFilters();
        this.tlt = tltName;
        this.currentSlot = switch (this.tlt) {
            case Head -> AttachmentsRegistry.CosmeticSlot.Haircuts;
            case General -> AttachmentsRegistry.CosmeticSlot.Underwears;
            case Torso -> AttachmentsRegistry.CosmeticSlot.Undertops;
            case Legs -> AttachmentsRegistry.CosmeticSlot.Pants;
            case Capes -> AttachmentsRegistry.CosmeticSlot.Capes;
            default -> null;
        };
        updateCamera();
        refreshUI();
    }

    private void setSubCategory(AttachmentsRegistry.CosmeticSlot slotName) {
        resetFilters();
        this.currentSlot = slotName;
        updateCamera();
        refreshUI();
    }

    private void handleEquipToggle(Ref<EntityStore> ref, Data data) {
        if (!Objects.equals(data.cosmeticId, originalId)) {
            this.gradientSet = null;
            this.variants = new HashMap<>();
            this.originalId = null;
        }

        if ("true".equals(data.enabled)) {
            // Unequip
            if (data.cosmeticId.isBlank()) {
                var current = AttachmentsRegistry.get().getEquipped(ref, currentSlot);
                if (current != null) AttachmentsRegistry.get().removeCosmetic(ref, current.getId(), !singleSelect);
            } else {
                AttachmentsRegistry.get().removeCosmetic(ref, data.cosmeticId, !singleSelect);
            }
            this.selectedId = data.cosmeticId;
        } else {
            // Equip
            if (data.cosmeticId == null || data.cosmeticId.isBlank()) {
                // Vanish logic
                var current = AttachmentsRegistry.get().getEquipped(ref, currentSlot);
                if (current != null)
                    AttachmentsRegistry.get().removeCosmetic(ref, current.getId(), !singleSelect);

                this.selectedId = null;
            } else {
                this.selectedId = data.cosmeticId;
                AttachmentsRegistry.get().addCosmetic(ref, data.cosmeticId, data.variantId, new AttachmentsRegistry.Colour(gradientSet, data.gradientId), !singleSelect);
            }
        }
        refreshUI();
    }

    private void resetFilters() {
        this.gradientSet = null;
        this.variants = new HashMap<>();
        this.originalId = null;
        this.selectedId = null;
        this.search = null;
    }

    private void refreshUI() {
        this.sendUpdate();
        this.rebuild();
    }

    @Override
    public void onDismiss(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store) {
        super.onDismiss(ref, store);
        this.playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, false, null));
    }

    public static class Data {
        public String action;
        public boolean multiSelect;
        public String search;
        public String variantId;
        public String gradientId;
        public AttachmentsRegistry.TopLevelTypes tlt;
        public AttachmentsRegistry.CosmeticSlot slot;
        public String cosmeticId;
        public String enabled;

        public static final Codec<AttachmentsRegistry.TopLevelTypes> CCC = new EnumCodec<>(AttachmentsRegistry.TopLevelTypes.class);
        public static final Codec<AttachmentsRegistry.CosmeticSlot> CCC2 = new EnumCodec<>(AttachmentsRegistry.CosmeticSlot.class);

        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("Action", BuilderCodec.STRING), (data, v) -> data.action = v, data -> data.action).add()
                .append(new KeyedCodec<>("@MultiSelect", BuilderCodec.BOOLEAN), (data, v) -> data.multiSelect = v, data -> data.multiSelect).add()
                .append(new KeyedCodec<>("@Search", BuilderCodec.STRING), (data, v) -> data.search = v, data -> data.search).add()
                .append(new KeyedCodec<>("VariantId", BuilderCodec.STRING), (data, v) -> data.variantId = v, data -> data.variantId).add()
                .append(new KeyedCodec<>("GradientId", BuilderCodec.STRING), (data, v) -> data.gradientId = v, data -> data.gradientId).add()
                .append(new KeyedCodec<>("TLT", CCC), (data, v) -> data.tlt = v, data -> data.tlt).add()
                .append(new KeyedCodec<>("Slot", CCC2), (data, v) -> data.slot = v, data -> data.slot).add()
                .append(new KeyedCodec<>("CosmeticId", BuilderCodec.STRING), (data, v) -> data.cosmeticId = v, data -> data.cosmeticId).add()
                .append(new KeyedCodec<>("Enabled", BuilderCodec.STRING), (data, v) -> data.enabled = v, data -> data.enabled).add()
                .build();
    }
}