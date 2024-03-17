package org.achymake.recovery.net;

import org.achymake.recovery.Recovery;
import org.achymake.recovery.data.Message;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public record UpdateChecker(Recovery plugin) {
    private String getName() {
        return plugin.getDescription().getName();
    }
    private String getVersion() {
        return plugin.getDescription().getVersion();
    }
    private Message getMessage() {
        return plugin.getMessage();
    }
    private boolean notifyUpdate() {
        return plugin.getConfig().getBoolean("notify-update");
    }
    public void getUpdate(Player player) {
        if (notifyUpdate()) {
            if (player.hasPermission("recovery.event.join.update")) {
                getLatest((latest) -> {
                    if (!getVersion().equals(latest)) {
                        getMessage().send(player,"&f" + getName() + "&6 has new update:");
                        getMessage().send(player,"- &a" + "https://www.spigotmc.org/resources/114623/");
                    }
                });
            }
        }
    }
    public void getUpdate() {
        if (notifyUpdate()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        if (getVersion().equals(latest)) {
                            getMessage().sendLog(Level.INFO, "You are using the latest version");
                        } else {
                            getMessage().sendLog(Level.INFO, getName() + " has new update:");
                            getMessage().sendLog(Level.INFO, "- https://www.spigotmc.org/resources/114623/");
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try {
            InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 114623)).openStream();
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            }
            inputStream.close();
        } catch (IOException e) {
            getMessage().sendLog(Level.WARNING, e.getMessage());
        }
    }
}
