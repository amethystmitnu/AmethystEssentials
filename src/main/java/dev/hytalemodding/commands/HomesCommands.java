package dev.hytalemodding.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.Home;
import dev.hytalemodding.HomeStore;

import javax.annotation.Nonnull;
import java.util.Map;

public class HomesCommands extends AbstractPlayerCommand {

    public HomesCommands() {
        super("homes", "List your homes");
        requirePermission(HomeStore.PERMISSION);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {
        Map<String, Home> homes = HomeStore.homesOf(
                store.getComponent(ref, UUIDComponent.getComponentType()).getUuid());

        if (homes.isEmpty()) {
            context.sendMessage(Message.raw("You have no homes yet. Use /sethome."));
        } else {
            context.sendMessage(Message.raw("Your homes (" + homes.size() + "/" + HomeStore.MAX_HOMES
                    + "): " + String.join(", ", homes.keySet())));
        }
    }
}