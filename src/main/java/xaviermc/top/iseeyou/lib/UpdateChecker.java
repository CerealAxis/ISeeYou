package xaviermc.top.iseeyou.lib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info("Failed to check for updates: " + exception.getMessage());
            }
        });
    }

    public void checkForUpdates() {
        getVersion(version -> {
            String currentVersion = "v1.1.6";
            if (currentVersion.compareToIgnoreCase(version) > 0) {
                plugin.getLogger().info("You are currently using a possible test version. For stability, please use the latest stable version!");
                plugin.getLogger().info("The current latest stable version is: " + version);
                String updateUrl = "https://www.spigotmc.org/resources/iseeyou-fork.115177/";
                plugin.getLogger().info("Update URL: " + updateUrl);
            } else if (currentVersion.equals(version)) {
                plugin.getLogger().info("No new updates available.");
            } else {
                plugin.getLogger().info("A new update is available. Version: " + version);
                String updateUrl = "https://www.spigotmc.org/resources/iseeyou-fork.115177/";
                plugin.getLogger().info("Update URL: " + updateUrl);
            }
        });
    }
}