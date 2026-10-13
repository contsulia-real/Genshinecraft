package dev.genshinecraft.world;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/** Server-authoritative query, administrative override, and requirement refresh. */
public final class WorldLevelCommands {
    private WorldLevelCommands() {}

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) ->
                dispatcher.register(Commands.literal("genshinecraft")
                        .then(Commands.literal("worldlevel")
                                .executes(ctx -> info(ctx.getSource()))
                                .then(Commands.literal("info").executes(ctx -> info(ctx.getSource())))
                                .then(Commands.literal("set")
                                        //? if >=26.3 {
                                        /*.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                        *///?} else {
                                        .requires(source -> source.hasPermission(2))
                                        //?}
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 9))
                                                .executes(ctx -> {
                                                    int level = IntegerArgumentType.getInteger(ctx, "level");
                                                    var data = StatueOfferings.get(ctx.getSource().getLevel());
                                                    data.setWorldLevel(level);
                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("World Level set to " + level), true);
                                                    return level + 1;
                                                })))
                                .then(Commands.literal("refresh")
                                        //? if >=26.3 {
                                        /*.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                        *///?} else {
                                        .requires(source -> source.hasPermission(2))
                                        //?}
                                        .executes(ctx -> {
                                            var data = StatueOfferings.get(ctx.getSource().getLevel());
                                            int level = data.refreshWorldLevel();
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal("World Level recalculated: " + level), true);
                                            return level + 1;
                                        })))));
    }

    private static int info(net.minecraft.commands.CommandSourceStack source) {
        var data = StatueOfferings.get(source.getLevel());
        int level = data.worldLevel();
        long xp = data.totalExperienceLevels();
        int statue = data.highestStatueLevel();
        String next = level >= 9 ? "MAX" : "Next: total XP levels "
                + xp + "/" + StatueOfferings.experienceRequirement(level + 1)
                + ", highest Statue of the Seven "
                + statue + "/" + StatueOfferings.statueRequirement(level + 1);
        source.sendSuccess(() -> Component.literal("World Level: " + level
                + " | Mob HP x" + (1 + level * 0.2)
                + " | Attack x" + (1 + level * 0.1)
                + " | Loot x" + (1 + level * 0.08)
                + " | " + next), false);
        return level + 1;
    }
}
