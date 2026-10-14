package dev.genshinecraft.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if =1.20.1 {
/*import net.minecraft.nbt.CompoundTag;
*///?} else {
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
//?}

import java.util.Optional;
import java.util.UUID;

public record CarrierData(
        UUID id, Element element, String skill, String burst,
        int skillLevel, int burstLevel, int elementalMastery,
        String statue, UUID owner, String ownerName, boolean abandoned
) {
    public static final String UNCLAIMABLE = "genshinecraft.unclaimable";

    public enum Element {
        ANEMO(0x73DDB9), GEO(0xE8BB65), ELECTRO(0xB78DEB),
        DENDRO(0x93CA65), HYDRO(0x64B5EF), PYRO(0xF08062),
        CRYO(0x9BD9F0);

        private final int color;

        Element(int color) {
            this.color = color;
        }

        public int color() {
            return color;
        }
    }

    //? if >=1.21.1 {
    private static final Codec<Element> ELEMENT_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            return DataResult.success(Element.valueOf(value));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Unknown elemental carrier element: " + value);
        }
    }, Element::name);

    public static final Codec<CarrierData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(CarrierData::id),
            ELEMENT_CODEC.fieldOf("element").forGetter(CarrierData::element),
            Codec.STRING.fieldOf("skill").forGetter(CarrierData::skill),
            Codec.STRING.fieldOf("burst").forGetter(CarrierData::burst),
            Codec.INT.fieldOf("skill_level").forGetter(CarrierData::skillLevel),
            Codec.INT.fieldOf("burst_level").forGetter(CarrierData::burstLevel),
            Codec.INT.fieldOf("elemental_mastery").forGetter(CarrierData::elementalMastery),
            Codec.STRING.fieldOf("statue").forGetter(CarrierData::statue),
            UUIDUtil.STRING_CODEC.optionalFieldOf("owner").forGetter(data -> Optional.ofNullable(data.owner())),
            Codec.STRING.fieldOf("owner_name").forGetter(CarrierData::ownerName),
            Codec.BOOL.fieldOf("abandoned").forGetter(CarrierData::abandoned)
    ).apply(instance, (id, element, skill, burst, skillLevel, burstLevel, mastery, statue, owner, ownerName, abandoned) ->
            new CarrierData(id, element, skill, burst, skillLevel, burstLevel, mastery,
                    statue, owner.orElse(null), ownerName, abandoned)));
    //?} else {
    /*private static final String PREFIX = "genshinecraft.";
    private static final String ID = PREFIX + "id";
    private static final String ELEMENT = PREFIX + "element";
    private static final String SKILL = PREFIX + "skill";
    private static final String BURST = PREFIX + "burst";
    private static final String SKILL_LEVEL = PREFIX + "skill_level";
    private static final String BURST_LEVEL = PREFIX + "burst_level";
    private static final String MASTERY = PREFIX + "elemental_mastery";
    private static final String STATUE = PREFIX + "statue";
    private static final String OWNER = PREFIX + "owner";
    private static final String OWNER_NAME = PREFIX + "owner_name";
    private static final String ABANDONED = PREFIX + "abandoned";
    *///?}

    public static ItemStack create(Item item, Element element, String skill, String burst,
                                   int skillLevel, int burstLevel, int elementalMastery, String statue) {
        if (!CarrierItems.isCarrier(item) || element == null || skill == null || skill.isBlank()
                || burst == null || burst.isBlank() || statue == null || statue.isBlank()
                || skillLevel < 0 || burstLevel < 0 || elementalMastery < 0
                || CarrierItems.fixedElement(item) != element) {
            throw new IllegalArgumentException("Invalid carrier data");
        }
        ItemStack stack = new ItemStack(item);
        write(stack, new CarrierData(UUID.randomUUID(), element, skill, burst,
                skillLevel, burstLevel, elementalMastery, statue, null, "", false));
        return stack;
    }

    /** Statue-issued carrier; skill and burst remain unassigned until implemented. */
    public static ItemStack createFromStatue(Item item, Element element, String statue) {
        if (!CarrierItems.isCarrier(item)
                || element == null || CarrierItems.fixedElement(item) != element
                || statue == null || statue.isBlank()) {
            throw new IllegalArgumentException("Invalid statue reward");
        }
        ItemStack stack = new ItemStack(item);
        write(stack, new CarrierData(UUID.randomUUID(), element, "", "",
                0, 0, 0, statue, null, "", false));
        return stack;
    }

    public static void setMastery(ItemStack stack, int target) {
        CarrierData data = read(stack);
        if (data != null && target != data.elementalMastery()) {
            write(stack, new CarrierData(data.id(), data.element(), data.skill(), data.burst(),
                    data.skillLevel(), data.burstLevel(), target,
                    data.statue(), data.owner(), data.ownerName(), data.abandoned()));
        }
    }

    public static CarrierData read(ItemStack stack) {
        if (!CarrierItems.isCarrier(stack)) {
            return null;
        }
        //? if =1.20.1 {
        /*CarrierData data = readNbt(stack);
        *///?} else {
        CarrierData data = stack.get(CarrierItems.carrierDataType());
        //?}
        return data != null && CarrierItems.fixedElement(stack.getItem()) == data.element() ? data : null;
    }

    public static boolean claim(ItemStack stack, net.minecraft.world.entity.player.Player player) {
        UUID playerId = player.getUUID();
        if (!CarrierItems.isCarrier(stack)) {
            return false;
        }
        // Chest loot stays ownerless even after its first inventory tick or a binding attempt.
        //? if =1.20.1 {
        /*if (stack.getTag() != null && stack.getTag().contains(UNCLAIMABLE)) return false;
        *///?} else {
        var customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.copyTag().contains(UNCLAIMABLE)) return false;
        //?}
        CarrierData data = read(stack);
        if (data == null) {
            // Refuse corrupted or mismatched existing data instead of resetting ownership.
            //? if =1.20.1 {
            /*CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(ID)) {
                return false;
            }
            *///?} else {
            if (stack.get(CarrierItems.carrierDataType()) != null) {
                return false;
            }
            //?}
            Element element = CarrierItems.fixedElement(stack.getItem());
            if (element == null) {
                return false;
            }
            data = new CarrierData(UUID.randomUUID(), element, "", "", 0, 0, 0, "", null, "", false);
        }
        if (data.owner() != null || data.abandoned()) {
            return false;
        }
        write(stack, new CarrierData(data.id(), data.element(), data.skill(), data.burst(),
                data.skillLevel(), data.burstLevel(), data.elementalMastery(),
                data.statue(), playerId, player.getName().getString(), false));
        return true;
    }

    public static boolean abandon(ItemStack stack, UUID playerId) {
        CarrierData data = read(stack);
        if (data == null || data.abandoned() || !playerId.equals(data.owner())) {
            return false;
        }
        write(stack, new CarrierData(data.id(), data.element(), data.skill(), data.burst(),
                data.skillLevel(), data.burstLevel(), data.elementalMastery(),
                data.statue(), data.owner(), data.ownerName(), true));
        return true;
    }

    public boolean usableBy(UUID playerId) {
        return !abandoned && playerId.equals(owner);
    }

    private static void write(ItemStack stack, CarrierData data) {
        //? if =1.20.1 {
        /*CompoundTag tag = stack.getOrCreateTag();
        tag.putString(ID, data.id().toString());
        tag.putString(ELEMENT, data.element().name());
        tag.putString(SKILL, data.skill());
        tag.putString(BURST, data.burst());
        tag.putInt(SKILL_LEVEL, data.skillLevel());
        tag.putInt(BURST_LEVEL, data.burstLevel());
        tag.putInt(MASTERY, data.elementalMastery());
        tag.putString(STATUE, data.statue());
        if (data.owner() != null) {
            tag.putString(OWNER, data.owner().toString());
            tag.putString(OWNER_NAME, data.ownerName());
        } else {
            tag.remove(OWNER);
            tag.remove(OWNER_NAME);
        }
        tag.putBoolean(ABANDONED, data.abandoned());
        *///?} else {
        stack.set(CarrierItems.carrierDataType(), data);
        //?}
    }

    //? if =1.20.1 {
    /*private static CarrierData readNbt(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ID) || !tag.contains(ELEMENT)
                || !tag.contains(SKILL) || !tag.contains(BURST) || !tag.contains(STATUE)) {
            return null;
        }
        try {
            String owner = tag.getString(OWNER);
            return new CarrierData(
                    UUID.fromString(tag.getString(ID)),
                    Element.valueOf(tag.getString(ELEMENT)),
                    tag.getString(SKILL), tag.getString(BURST),
                    tag.getInt(SKILL_LEVEL), tag.getInt(BURST_LEVEL),
                    tag.getInt(MASTERY), tag.getString(STATUE),
                    owner.isEmpty() ? null : UUID.fromString(owner),
                    tag.getString(OWNER_NAME),
                    tag.getBoolean(ABANDONED)
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
    *///?}
}
