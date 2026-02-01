package com.goodwitchlalya.lalyan_cosmetic_core.command;

import com.goodwitchlalya.lalyan_cosmetic_core.component.UnlockedCosmeticsComponent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;

/**
 * Subcommand to manually apply a cosmetic to a player.
 * Requires OP permissions.
 */
public class CosmeticGiveCommand extends AbstractPlayerCommand {
    private RequiredArg<String> cosmeticName;

    /**
     * Constructor for the 'apply' subcommand.
     * Defines the command's arguments and permissions.
     */
    public CosmeticGiveCommand() {
        super("give", "Unlocks a cosmetic for the player");
        this.cosmeticName = this.withRequiredArg("cosmetic name", "The cosmetic Id", ArgTypes.STRING);
        this.setPermissionGroups("OP");
    }

    /**
     * Executes the command logic.
     * Applies the specified cosmetic to the player who executed the command.
     */
    @Override
    protected void execute(@NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        var unlocked = store.ensureAndGetComponent(ref, UnlockedCosmeticsComponent.getComponentType());
        var name = this.cosmeticName.get(commandContext);
        if (name != null) {
            if (unlocked.add(this.cosmeticName.get(commandContext))) {
                commandContext.sendMessage(Message.raw("Added "+ name).color(Color.GREEN));

                store.replaceComponent(ref, UnlockedCosmeticsComponent.getComponentType(), unlocked);
            }
        }
    }
}