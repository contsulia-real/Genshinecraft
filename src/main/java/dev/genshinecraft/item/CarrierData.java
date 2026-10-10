package dev.genshinecraft.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//?}

import java.util.UUID;

public record CarrierData(
        UUID id, Element element, String skill, String burst,
        int skillLevel, int burstLevel, int elementalMastery,
        String statue, UUID owner, boolean abandoned
) {
    public enum Element {
        ANEMO, GEO, ELECTRO, DENDRO, HYDRO, PYRO, CRYO
    }

    private static final String PREFIX = "genshinecraft.";
    private static final String ID = PREFIX + "id";
    private static final String ELEMENT = PREFIX + "element";
    private static final String SKILL = PREFIX + "skill";
    private static final String BURST = PREFIX + "burst";
    private static final String SKILL_LEVEL = PREFIX + "skill_level";
    private static final String BURST_LEVEL = PREFIX + "burst_level";
    private static final String MASTERY = PREFIX + "elemental_mastery";
    private static final String STATUE = PREFIX + "statue";
    private static final String OWNER = PREFIX + "owner";
    private static final String ABANDONED = PREFIX + "abandoned";

    public static ItemStack create(Item item, Element element, String skill, String burst,
                                   int skillLevel, int burstLevel, int elementalMastery, String statue) {
        if (!CarrierItems.isCarrier(item) || element == null || skill == null || skill.isBlank()
                || burst == null || burst.isBlank() || statue == null || statue.isBlank()
                || skillLevel < 0 || burstLevel < 0 || elementalMastery < 0
                || (CarrierItems.fixedElement(item) != null && CarrierItems.fixedElement(item) != element)) {
            throw new IllegalArgumentException("Invalid carrier data");
        }
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = readTag(stack);
        tag.putString(ID, UUID.randomUUID().toString());
        tag.putString(ELEMENT, element.name());
        tag.putString(SKILL, skill);
        tag.putString(BURST, burst);
        tag.putInt(SKILL_LEVEL, skillLevel);
        tag.putInt(BURST_LEVEL, burstLevel);
        tag.putInt(MASTERY, elementalMastery);
        tag.putString(STATUE, statue);
        saveTag(stack, tag);
        return stack;
    }

    public static CarrierData read(ItemStack stack) {
        if (!CarrierItems.isCarrier(stack)) {
            return null;
        }
        CompoundTag tag = readTag(stack);
        if (!tag.contains(ID) || !tag.contains(ELEMENT) || !tag.contains(SKILL)
                || !tag.contains(BURST) || !tag.contains(STATUE)) {
            return null;
        }
        try {
            String owner = string(tag, OWNER);
            CarrierData parsed = new CarrierData(
                    UUID.fromString(string(tag, ID)),
                    Element.valueOf(string(tag, ELEMENT)),
                    string(tag, SKILL), string(tag, BURST),
                    integer(tag, SKILL_LEVEL), integer(tag, BURST_LEVEL),
                    integer(tag, MASTERY), string(tag, STATUE),
                    owner.isEmpty() ? null : UUID.fromString(owner),
                    bool(tag, ABANDONED)
            );
            Element fixed = CarrierItems.fixedElement(stack.getItem());
            return fixed == null || fixed == parsed.element ? parsed : null;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public static boolean claim(ItemStack stack, UUID playerId) {
        if (!CarrierItems.isCarrier(stack)) {
            return false;
        }
        CompoundTag tag = readTag(stack);
        if (tag.contains(OWNER) || bool(tag, ABANDONED)) {
            return false;
        }
        if (read(stack) == null) {
            // Only new element-specific items receive initial data automatically.
            // Unassigned skills/energy stay unavailable until the skill system exists.
            Element fixed = CarrierItems.fixedElement(stack.getItem());
            if (tag.contains(ID) || fixed == null) {
                return false;
            }
            tag.putString(ID, UUID.randomUUID().toString());
            tag.putString(ELEMENT, fixed.name());
            tag.putString(SKILL, "");
            tag.putString(BURST, "");
            tag.putInt(SKILL_LEVEL, 0);
            tag.putInt(BURST_LEVEL, 0);
            tag.putInt(MASTERY, 0);
            tag.putString(STATUE, "");
        }
        tag.putString(OWNER, playerId.toString());
        saveTag(stack, tag);
        return true;
    }

    public static boolean abandon(ItemStack stack, UUID playerId) {
        CarrierData data = read(stack);
        if (data == null || data.abandoned || !playerId.equals(data.owner)) {
            return false;
        }
        CompoundTag tag = readTag(stack);
        tag.putBoolean(ABANDONED, true);
        saveTag(stack, tag);
        return true;
    }

    public boolean usableBy(UUID playerId) {
        return !abandoned && playerId.equals(owner);
    }

    private static CompoundTag readTag(ItemStack stack) {
        //? if =1.20.1 {
        /*return stack.getOrCreateTag();
        *///?} else {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
        //?}
    }

    private static void saveTag(ItemStack stack, CompoundTag tag) {
        //? if >=1.21.1 {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        //?}
    }

    private static String string(CompoundTag tag, String key) {
        //? if >=26.3 {
        /*return tag.getStringOr(key, "");
        *///?} else {
        return tag.getString(key);
        //?}
    }

    private static int integer(CompoundTag tag, String key) {
        //? if >=26.3 {
        /*return tag.getIntOr(key, 0);
        *///?} else {
        return tag.getInt(key);
        //?}
    }

    private static boolean bool(CompoundTag tag, String key) {
        //? if >=26.3 {
        /*return tag.getBooleanOr(key, false);
        *///?} else {
        return tag.getBoolean(key);
        //?}
    }
}
