package com.goodwitchlalya.lalyan_cosmetic_core.interaction;

import com.goodwitchlalya.lalyan_cosmetic_core.component.UnlockedCosmeticsComponent;
import com.goodwitchlalya.lalyan_cosmetic_core.util.TinyMsg;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UnlockCosmeticInteraction extends SimpleInstantInteraction {

    private String cosmeticId;
    private String[] message;

    public static final BuilderCodec<UnlockCosmeticInteraction> CODEC = BuilderCodec.builder(UnlockCosmeticInteraction.class, UnlockCosmeticInteraction::new)
            .append(new KeyedCodec<>("UnlockCosmetic", Codec.STRING), (o, v) -> o.cosmeticId = v, (o) -> o.cosmeticId)
            .documentation("Cosmetic to unlock")
            .add()

            .append(new KeyedCodec<>("Message", Codec.STRING_ARRAY), (o, v) -> o.message = v, (o) -> o.message)
            .documentation("Messages to send to the player when unlocked successfully. Supports formatting (<i><b><green>...)")
            .add()

            .build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext ctx, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = ctx.getEntity();
        CommandBuffer<EntityStore> commandBuffer = ctx.getCommandBuffer();
        if (commandBuffer == null) {
            return;
        }

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        var comp = commandBuffer.ensureAndGetComponent(ref, UnlockedCosmeticsComponent.getComponentType());
        if (comp.add(cosmeticId)) {
            consumeHeldItem(ctx);

            if (message != null) {
                for (String s : message) {
                    player.sendMessage(TinyMsg.parse(s));
                }
            }

            commandBuffer.replaceComponent(ref, UnlockedCosmeticsComponent.getComponentType(), comp);
        }
    }

    private void consumeHeldItem(InteractionContext context) {
        ItemStack heldItem = context.getHeldItem();
        if (heldItem == null) return;

        int currentQuantity = heldItem.getQuantity();
        if (currentQuantity <= 1) {
            context.setHeldItem(null);

            ItemContainer container = context.getHeldItemContainer();
            if (container != null) {
                byte slot = context.getHeldItemSlot();
                container.removeItemStackFromSlot(slot);
            }
        } else {
            ItemStack newStack = new ItemStack(heldItem.getItemId(), currentQuantity - 1);
            context.setHeldItem(newStack);

            ItemContainer container = context.getHeldItemContainer();
            if (container != null) {
                byte slot = context.getHeldItemSlot();
                container.setItemStackForSlot(slot, newStack);
            }
        }
    }
}
