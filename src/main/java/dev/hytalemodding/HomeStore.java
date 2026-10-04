package dev.hytalemodding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// File format, one line per home:  uuid|name|world|x|y|z|pitch|yaw|roll
// Old 5-field lines (uuid|name|x|y|z) still load, with no world and no facing.
public class HomeStore {
    public static final int MAX_HOMES = 5;
    // One permission node shared by all four home commands
    public static final String PERMISSION = "ame.amethystessentials.home";

    private static final Map<UUID, Map<String, Home>> HOMES = new ConcurrentHashMap<>();
    private static Path file;

    public static synchronized void init(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            file = dataDir.resolve("homes.txt");
            if (!Files.exists(file)) return;

            for (String line : Files.readAllLines(file)) {
                String[] p = line.split("\\|");
                try {
                    UUID id = UUID.fromString(p[0]);
                    if (p.length == 9) {
                        homesOf(id).put(p[1], new Home(p[2],
                                Double.parseDouble(p[3]), Double.parseDouble(p[4]), Double.parseDouble(p[5]),
                                Float.parseFloat(p[6]), Float.parseFloat(p[7]), Float.parseFloat(p[8])));
                    } else if (p.length == 5) { // old format
                        homesOf(id).put(p[1], new Home("",
                                Double.parseDouble(p[2]), Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                                0f, 0f, 0f));
                    }
                } catch (IllegalArgumentException ignored) { } // skip damaged lines
            }
        } catch (IOException e) {
            e.printStackTrace(); // a bad file must never stop the world loading
        }
    }

    private static synchronized void save() {
        if (file == null) return;
        try {
            List<String> lines = new ArrayList<>();
            for (Map.Entry<UUID, Map<String, Home>> player : HOMES.entrySet()) {
                for (Map.Entry<String, Home> e : player.getValue().entrySet()) {
                    Home h = e.getValue();
                    lines.add(player.getKey() + "|" + e.getKey() + "|" + h.world() + "|"
                            + h.x() + "|" + h.y() + "|" + h.z() + "|"
                            + h.pitch() + "|" + h.yaw() + "|" + h.roll());
                }
            }
            Path tmp = file.resolveSibling("homes.txt.tmp");
            Files.write(tmp, lines);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String clean(String s) {
        return s.replace("|", "").replace("\n", "").replace("\r", ""); // would break the file format
    }

    public static synchronized Map<String, Home> homesOf(UUID id) {
        return HOMES.computeIfAbsent(id, k -> new LinkedHashMap<>());
    }

    // Returns false if the player already has MAX_HOMES and this would be a new one
    public static synchronized boolean set(UUID id, String name, Home home) {
        name = clean(name);
        home = new Home(clean(home.world()), home.x(), home.y(), home.z(), home.pitch(), home.yaw(), home.roll());
        Map<String, Home> homes = homesOf(id);
        if (!homes.containsKey(name) && homes.size() >= MAX_HOMES) return false;
        homes.put(name, home);
        save();
        return true;
    }

    public static synchronized boolean remove(UUID id, String name) {
        boolean removed = homesOf(id).remove(name) != null;
        if (removed) save();
        return removed;
    }
}