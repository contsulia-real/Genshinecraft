package dev.genshinecraft.world;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
//? if >=26.3 {
/*import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.util.datafix.DataFixTypes;
*///?} else {
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
//? if =1.21.1 {
import net.minecraft.core.HolderLookup;
import net.minecraft.util.datafix.DataFixTypes;
//?}
//?}

import java.util.HashMap;
import java.util.Map;

/** Per-statue levels and partial offerings, shared across players and persisted in the world. */
public final class StatueOfferings extends SavedData {
    private static final String FILE = "genshinecraft_statue_offerings";
    //? if >=26.3 {
    /*private static final TagKey<Structure> MOON_STRUCTURES = TagKey.create(Registries.STRUCTURE,
            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "moon_statues"));
    *///?} else if =1.21.1 {
    private static final TagKey<Structure> MOON_STRUCTURES = TagKey.create(Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, "moon_statues"));
    //?} else {
    /*private static final TagKey<Structure> MOON_STRUCTURES = TagKey.create(Registries.STRUCTURE,
            new ResourceLocation(Genshinecraft.MOD_ID, "moon_statues"));
    *///?}
    private final Map<String, String> entries;

    public StatueOfferings() {
        this.entries = new HashMap<>();
    }

    private StatueOfferings(Map<String, String> entries) {
        this.entries = new HashMap<>(entries);
    }

    //? if >=26.3 {
    /*private static final Codec<StatueOfferings> CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .xmap(StatueOfferings::new, data -> Map.copyOf(data.entries));
    private static final SavedDataType<StatueOfferings> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "statue_offerings"),
            StatueOfferings::new, CODEC, DataFixTypes.LEVEL);
    *///?} else {
    private static StatueOfferings load(CompoundTag tag) {
        Map<String, String> saved = new HashMap<>();
        CompoundTag all = tag.getCompound("offerings");
        for (String id : all.getAllKeys()) saved.put(id, all.getString(id));
        return new StatueOfferings(saved);
    }

    //? if =1.20.1 {
    /*@Override
    public CompoundTag save(CompoundTag tag) {
        return saveValues(tag);
    }
    *///?} else {
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        return saveValues(tag);
    }
    //?}

    private CompoundTag saveValues(CompoundTag tag) {
        CompoundTag all = new CompoundTag();
        entries.forEach(all::putString);
        tag.put("offerings", all);
        return tag;
    }
    //?}

    public static StatueOfferings get(ServerLevel level) {
        var storage = level.getServer().overworld().getDataStorage();
        //? if >=26.3 {
        /*return storage.computeIfAbsent(TYPE);
        *///?} else if =1.21.1 {
        return storage.computeIfAbsent(
                new SavedData.Factory<>(StatueOfferings::new,
                        (tag, registries) -> load(tag), DataFixTypes.LEVEL), FILE);
        //?} else {
        /*return storage.computeIfAbsent(StatueOfferings::load, StatueOfferings::new, FILE);
        *///?}
    }

    public static String id(Level level, BlockPos pos) {
        return level.dimension().toString() + "@" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    /** The moon statue account is shared across all moon statues, but only for this player. */
    public static String moonId(Player player) {
        return "moon_player@" + player.getUUID();
    }

    /** Record real placed/generated moon cores for the nearest-shrine teleport. */
    public void recordMoon(ServerLevel level, BlockPos pos) {
        String key = "moon_location@" + id(level, pos);
        if (!entries.containsKey(key)) {
            entries.put(key, "1");
            setDirty();
        }
    }

    public BlockPos nearestMoon(ServerLevel level, BlockPos origin) {
        String prefix = "moon_location@" + level.dimension() + "@";
        BlockPos closest = null;
        double distance = Double.MAX_VALUE;
        for (String key : entries.keySet()) {
            if (!key.startsWith(prefix)) continue;
            String[] xyz = key.substring(prefix.length()).split(",");
            if (xyz.length != 3) continue;
            try {
                BlockPos candidate = new BlockPos(Integer.parseInt(xyz[0]),
                        Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
                double squared = candidate.distSqr(origin);
                if (squared >= distance || level.getBlockState(candidate).getBlock()
                        != StatueBlocks.MOON_ALTAR.get()) continue;
                distance = squared;
                closest = candidate;
            } catch (NumberFormatException ignored) {
            }
        }
        // Generated structures might have been placed by WorldGenRegion (not ServerLevel),
        // so their altar's onPlace event might not be recorded in persistent data.
        BlockPos structure = level.findNearestMapStructure(MOON_STRUCTURES, origin, 64, false);
        if (structure != null && structure.distSqr(origin) < distance + 700) {
            BlockPos discovered = findMoonCore(level, structure);
            if (discovered != null && discovered.distSqr(origin) < distance) {
                recordMoon(level, discovered);
                closest = discovered;
            }
        }
        return closest;
    }

    private static BlockPos findMoonCore(ServerLevel level, BlockPos near) {
        // The moon statue template is 17x19x17; the start position isn't necessarily the core.
        for (int dx = -18; dx <= 18; dx++) {
            for (int dz = -18; dz <= 18; dz++) {
                int x = near.getX() + dx, z = near.getZ() + dz;
                int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                //? if >=26.3 {
                /*int minimumY = level.getMinY();
                *///?} else {
                int minimumY = level.getMinBuildHeight();
                //?}
                for (int y = top; y >= top - 35 && y >= minimumY; y--) {
                    BlockPos position = new BlockPos(x, y, z);
                    if (level.getBlockState(position).getBlock() == StatueBlocks.MOON_ALTAR.get()) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    public static boolean authentic(Player player, BlockPos pos, CarrierData.Element element,
                                    InteractionHand hand) {
        CarrierData data = CarrierData.read(player.getItemInHand(hand));
        return data != null && data.element() == element && data.usableBy(player.getUUID())
                && data.statue().equals(id(player.level(), pos));
    }

    public record Progress(int level, long contributed) {
        static final Progress INITIAL = new Progress(1, 0);
    }

    public Progress progress(String source) {
        String raw = entries.get(source);
        if (raw == null) return Progress.INITIAL;
        String[] parts = raw.split(",", -1);
        if (parts.length != 2) return Progress.INITIAL;
        try {
            int level = Integer.parseInt(parts[0]);
            long contributed = Long.parseLong(parts[1]);
            if (level >= 1 && contributed >= 0 && contributed < cost((long) level + 1)) {
                return new Progress(level, contributed);
            }
        } catch (NumberFormatException ignored) {
        }
        return Progress.INITIAL;
    }

    /** Initially up to 50; after level 50 each completed level unlocks its successor. */
    public static long unlocked(int level) {
        return Math.max(50L, (long) level + 1);
    }

    public static long cost(long targetLevel) {
        return 4 + targetLevel / 3 + targetLevel * targetLevel / 250;
    }

    public static int mastery(int level) {
        long steps = (long) level - 1;
        return (int) Math.min(Integer.MAX_VALUE, 2 * steps + steps / 5);
    }

    /** The UI never exposes inventory slots; this is the only authoritative sigil count. */
    public static int countSigils(Player player, CarrierData.Element element) {
        var inventory = player.getInventory();
        Item sigil = element == null ? CarrierItems.MOON_SIGIL.get()
                : CarrierItems.SIGILS.get(element).get();
        int result = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(sigil)) result += stack.getCount();
        }
        return result;
    }

    /**
     * One-level action requires enough sigils and never partially pays.
     * All action consumes all matching sigils, advancing as many levels as possible,
     * and preserving any remainder as contributed progress.
     */
    public int donate(ServerLevel level, BlockPos pos, CarrierData.Element element,
                      Player player, InteractionHand hand, boolean all) {
        if (player.level() != level || player.blockPosition().distSqr(pos) > 64) return 0;
        if (element == null) {
            if (level.getBlockState(pos).getBlock() != StatueBlocks.MOON_ALTAR.get()) return 0;
        } else {
            if (level.getBlockState(pos).getBlock() != StatueBlocks.ALTARS.get(element).get()
                    || !authentic(player, pos, element, hand)) return 0;
        }
        String source = element == null ? moonId(player) : id(level, pos);
        Progress current = progress(source);
        if (current.level() == Integer.MAX_VALUE) return 0;
        long remaining = cost((long) current.level() + 1) - current.contributed();
        if (remaining <= 0) return 0;

        int available = countSigils(player, element);
        if (!all && available < remaining) return 0;
        long budget = all ? available : remaining;
        if (budget <= 0) return 0;

        var inventory = player.getInventory();
        Item sigil = element == null ? CarrierItems.MOON_SIGIL.get()
                : CarrierItems.SIGILS.get(element).get();
        int taken = 0;
        for (int slot = 0; slot < inventory.getContainerSize() && budget > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(sigil)) continue;
            int count = (int) Math.min(budget, stack.getCount());
            if (count <= 0) continue;
            stack.shrink(count);
            budget -= count;
            taken += count;
        }
        if (taken == 0) return 0;
        inventory.setChanged();

        long updated = current.contributed() + taken;
        int nextLevel = current.level();
        while (nextLevel < Integer.MAX_VALUE) {
            long needed = cost((long) nextLevel + 1);
            if (updated < needed) break;
            updated -= needed;
            nextLevel++;
        }
        entries.put(source, nextLevel + "," + updated);
        setDirty();
        for (long milestone = (long) current.level() + 1; milestone <= nextLevel; milestone++) {
            // Award every completed level exactly once, even during a multi-level donation.
            player.giveExperiencePoints(experience((int) milestone));
            if (element != null && milestone % 10 == 0) {
                give(level, player, CarrierItems.MOON_SIGIL.get(), 3);
            }
            if (element == null) {
                give(level, player, Items.EMERALD, 1 + (milestone % 5 == 0 ? 3 : 0));
            }
        }
        syncCarriers(player);
        return taken;
    }

    /** Experience points granted when the statue reaches the target level. */
    public static int experience(int level) {
        return (int) Math.min(Integer.MAX_VALUE, 5L + 2L * Math.max(0L, (long) level - 1));
    }

    private static void give(ServerLevel level, Player player, Item item, int quantity) {
        ItemStack reward = new ItemStack(item, quantity);
        player.addItem(reward);
        if (!reward.isEmpty()) {
            level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level,
                    player.getX(), player.getY() + 0.5, player.getZ(), reward));
        }
    }

    /** Every one of the player's valid carriers receives the full moon bonus. */
    public int moonMastery(Player player) {
        return mastery(progress(moonId(player)).level());
    }

    public static void syncCarriers(Player player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        StatueOfferings offerings = get(level);
        var inventory = player.getInventory();
        int moonBonus = offerings.moonMastery(player);
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            CarrierData data = CarrierData.read(stack);
            if (data == null || !data.usableBy(player.getUUID())) continue;
            int statueBonus = data.statue().isBlank() ? 0
                    : mastery(offerings.progress(data.statue()).level());
            long total = (long) statueBonus + moonBonus;
            // Each carrier is updated independently with the full value, never a divided pool.
            CarrierData.setMastery(stack, (int) Math.min(Integer.MAX_VALUE, total));
        }
    }
}
