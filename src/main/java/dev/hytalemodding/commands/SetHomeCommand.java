package dev.hytalemodding.commands;

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
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import dev.hytalemodding.Home;
import dev.hytalemodding.HomeStore;
// RequiredArg: Alt+Enter to import, as before

import javax.annotation.Nonnull;

public class SetHomeCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> nameArg;

    public SetHomeCommand() {
        super("sethome", "Save your current location as a home");
        requirePermission(HomeStore.PERMISSION); // new line
        this.nameArg = this.withRequiredArg("name", "Name for this home", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef,
                           @Nonnull World world) {
        UUIDComponent id = store.getComponent(ref, UUIDComponent.getComponentType());
        String name = nameArg.get(context).trim().toLowerCase();

        // playerRef.getTransform().getPosition() is the same call the game's own code uses
        var transform = playerRef.getTransform();
        var pos = transform.getPosition();
        // GUESS: rotation accessors. Type "rot." and pick from autocomplete if red
        // (could be pitch()/yaw()/roll() or getPitch()/getYaw()/getRoll()).
        var rot = transform.getRotation();

        Home home = new Home(world.getName(), pos.x, pos.y, pos.z, rot.pitch(), rot.yaw(), rot.roll());
        boolean saved = HomeStore.set(id.getUuid(), name, home);

        if (saved) {
            context.sendMessage(Message.raw("Home '" + name + "' set!"));
        } else {
            context.sendMessage(Message.raw("You already have " + HomeStore.MAX_HOMES
                    + " homes. Use /delhome <name> to free one up."));
        }
    }
}