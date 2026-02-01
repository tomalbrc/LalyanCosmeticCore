package com.goodwitchlalya.lalyan_cosmetic_core;

import com.goodwitchlalya.lalyan_cosmetic_core.command.CosmeticCommand;
import com.goodwitchlalya.lalyan_cosmetic_core.component.CosmeticComponent;
import com.goodwitchlalya.lalyan_cosmetic_core.component.UnlockedCosmeticsComponent;
import com.goodwitchlalya.lalyan_cosmetic_core.interaction.OpenCosmeticPageInteraction;
import com.goodwitchlalya.lalyan_cosmetic_core.interaction.UnlockCosmeticInteraction;
import com.goodwitchlalya.lalyan_cosmetic_core.util.AttachmentsRegistry;
import com.goodwitchlalya.lalyan_cosmetic_core.util.CustomCosmetic;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

/**
 * Main class for the Lalyan Cosmetic Core plugin.
 * This class handles the plugin's lifecycle, including initialization, setup, and shutdown.
 */
public class CosmeticCore extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    
    /**
     * Constructor for the plugin.
     * @param init The initialization context provided by the Hytale server.
     */
    public CosmeticCore(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    /**
     * Called to set up the plugin's components.
     * This method registers commands, components, interactions, and event listeners.
     */
    @Override
    protected void setup() {
        // Register the custom component for storing cosmetic data on entities.
        CosmeticComponent.setup(this.getEntityStoreRegistry());
        UnlockedCosmeticsComponent.setup(this.getEntityStoreRegistry());

        this.getAssetRegistry().register(HytaleAssetStore.builder(CustomCosmetic.class, new DefaultAssetMap<>()).setPath("CustomCosmetics").setCodec(CustomCosmetic.CODEC).setKeyFunction(CustomCosmetic::getId).loadsAfter(Item.class).build());

        // Register the main command for the plugin.
        this.getCommandRegistry().registerCommand(new CosmeticCommand());
        
        // Register the custom interaction for opening the cosmetic GUI.
        getCodecRegistry(Interaction.CODEC).register("LCC_OpenCosmetics", OpenCosmeticPageInteraction.class, OpenCosmeticPageInteraction.CODEC);
        getCodecRegistry(Interaction.CODEC).register("UnlockCosmetic", UnlockCosmeticInteraction.class, UnlockCosmeticInteraction.CODEC);

        // Register an event listener for when a player is ready, to apply their saved cosmetics.
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            AttachmentsRegistry.get().rebuildSkinWithCosmetics(event.getPlayerRef());
        });
    }
}