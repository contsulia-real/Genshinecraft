package dev.genshinecraft.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Local-only setting; F9 toggles it and writes a simple editable config file. */
public final class DamageNumberSettings {
    private static final Path FILE = Path.of("config", "genshinecraft-client.properties");
    private static boolean enabled = true;

    private DamageNumberSettings() {
    }

    public static void load() {
        Properties properties = new Properties();
        if (Files.isRegularFile(FILE)) {
            try (var input = Files.newInputStream(FILE)) {
                properties.load(input);
                enabled = Boolean.parseBoolean(properties.getProperty("damageNumbers", "true"));
            } catch (IOException ignored) {
                // No configuration should prevent starting a client.
            }
        }
    }

    public static boolean enabled() {
        return enabled;
    }

    public static void toggle() {
        enabled = !enabled;
        Properties properties = new Properties();
        properties.setProperty("damageNumbers", Boolean.toString(enabled));
        try {
            Files.createDirectories(FILE.getParent());
            try (var output = Files.newOutputStream(FILE)) {
                properties.store(output, "Genshinecraft client visual settings");
            }
        } catch (IOException ignored) {
            // Keep the live setting even when a config directory is read-only.
        }
    }
}
