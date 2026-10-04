package dev.hytalemodding.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.HomeStore;

import javax.annotation.Nonnull;

public class DelHomeCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> nameArg;

    public DelHomeCommand() {
        super("delhome", "Delete one of your homes");
        requirePermission(HomeStore.PERMISSION);
        this.nameArg = this.withRequiredArg("name", "Which home to delete", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {
        String name = nameArg.get(context).trim().toLowerCase();
        boolean removed = HomeStore.remove(
                store.getComponent(ref, UUIDComponent.getComponentType()).getUuid(), name);

        context.sendMessage(Message.raw(removed ? "Deleted home '" + name + "'."
                : "No home named '" + name + "'."));
    }
}