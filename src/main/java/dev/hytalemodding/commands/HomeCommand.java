package dev.hytalemodding.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.Home;
import dev.hytalemodding.HomeStore;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;

public class HomeCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> nameArg;

    public HomeCommand() {
        super("home", "Teleport to one of your homes");
        requirePermission(HomeStore.PERMISSION);
        this.nameArg = this.withRequiredArg("name", "Which home", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {
        UUID uuid = store.getComponent(ref, UUIDComponent.getComponentType()).getUuid();
        Map<String, Home> homes = HomeStore.homesOf(uuid);

        String name = nameArg.get(context).trim().toLowerCase();
        Home home = homes.get(name);

        if (home == null) {
            String list = homes.isEmpty() ? "none yet, use /sethome <name>" : String.join(", ", homes.keySet());
            context.sendMessage(Message.raw("No home named '" + name + "'. Your homes: " + list));
            return;
        }

        // Old homes have no world saved, so they use the world you're standing in now
        World target = world;
        if (!home.world().isEmpty()) {
            target = Universe.get().getWorld(home.world());
            if (target == null) {
                context.sendMessage(Message.raw("That home is in the world '" + home.world()
                        + "', which isn't loaded."));
                return;
            }
        }

        Teleport teleport = Teleport.createForPlayer(target,
                new Vector3d(home.x(), home.y(), home.z()),
                new Rotation3f(home.pitch(), home.yaw(), home.roll()));
        store.addComponent(ref, Teleport.getComponentType(), teleport);

        context.sendMessage(Message.raw("Welcome home! (" + name + ")"));
    }
}