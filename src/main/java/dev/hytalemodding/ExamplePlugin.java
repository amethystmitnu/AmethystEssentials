package dev.hytalemodding;

import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.hytalemodding.commands.*;
import dev.hytalemodding.config.ExampleConfig;

import javax.annotation.Nonnull;
import java.util.Set;

public class ExamplePlugin extends JavaPlugin {

    private static Config<ExampleConfig> config = null;

    public ExamplePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("example_config", ExampleConfig.CODEC);
    }

    @Override
    protected void setup() {
        config.save();

        // Load saved homes from the plugin's data folder
        HomeStore.init(getDataDirectory());

        // Everyone gets the one node that unlocks all four home commands
        PermissionsModule.get().addGroupPermission("Default", Set.of(HomeStore.PERMISSION));

        this.getCommandRegistry().registerCommand(new SetHomeCommand());
        this.getCommandRegistry().registerCommand(new HomeCommand());
        this.getCommandRegistry().registerCommand(new HomesCommands());
        this.getCommandRegistry().registerCommand(new DelHomeCommand());
    }

    public static Config<ExampleConfig> getConfig() {
        return config;
    }
}