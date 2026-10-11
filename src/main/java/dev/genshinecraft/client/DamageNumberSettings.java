package dev.genshinecraft.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.BooleanSupplier;

/** Local-only setting managed by the mod configuration screen. */
public final class DamageNumberSettings {
    private static final Path FILE = Path.of("config", "genshinecraft-client.properties");
    private static boolean enabled = true;
    private static BooleanSupplier nativeConfig;

    private DamageNumberSettings() {
    }

    /** Forge and NeoForge expose their own ConfigSpec to Configured or NeoForge UI. */
    public static void useNativeConfig(BooleanSupplier getter) {
        nativeConfig = getter;
    }

    public static void load() {
        if (nativeConfig != null) {
            return;
        }
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
        if (nativeConfig != null) {
            try {
                return nativeConfig.getAsBoolean();
            } catch (IllegalStateException ignored) {
                return true;
            }
        }
        return enabled;
    }

    public static void setEnabled(boolean value) {
        if (enabled == value) {
            return;
        }
        enabled = value;
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
