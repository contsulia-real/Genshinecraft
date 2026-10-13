package dev.genshinecraft.item;

import com.mojang.serialization.Codec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
//? if >=1.21.1 {
import net.minecraft.world.item.MaceItem;
import net.minecraft.core.UUIDUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

import java.util.UUID;

public record WeaponBinding(UUID carrierId, UUID owner, CarrierData.Element element, String carrierNameKey) {
    //? if >=1.21.1 {
    private static final Codec<CarrierData.Element> ELEMENT_CODEC = Codec.STRING.comapFlatMap(value -> {
        try {
            return DataResult.success(CarrierData.Element.valueOf(value));
        } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Unknown bound element: " + value);
        }
    }, CarrierData.Element::name);

    public static final Codec<WeaponBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("carrier_id").forGetter(WeaponBinding::carrierId),
            UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(WeaponBinding::owner),
            ELEMENT_CODEC.fieldOf("element").forGetter(WeaponBinding::element),
            Codec.STRING.fieldOf("carrier_name").forGetter(WeaponBinding::carrierNameKey)
    ).apply(instance, WeaponBinding::new));
    //?}

    public static boolean isWeapon(ItemStack stack) {
        if (stack.isEmpty() || CarrierItems.isCarrier(stack)) {
            return false;
        }
        return stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES)
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem
                || stack.getItem() instanceof TridentItem
                //? if >=1.21.1 {
                || stack.getItem() instanceof MaceItem
                //?}
                //? if >=26.3 {
                /*|| stack.is(ItemTags.SPEARS)
                *///?}
                ;
    }

    // Called on the server, from vanilla's stacked-on-other inventory click.
    public static boolean bind(ItemStack weapon, ItemStack carrier, Player player) {
        if (!isWeapon(weapon)) {
            return false;
        }
        CarrierData.claim(carrier, player);
        CarrierData data = CarrierData.read(carrier);
        if (data == null || !data.usableBy(player.getUUID())) {
            return false;
        }
        write(weapon, new WeaponBinding(data.id(), player.getUUID(), data.element(),
                carrier.getItem().getDescriptionId()));
        return true;
    }

    public static WeaponBinding read(ItemStack weapon) {
        if (!isWeapon(weapon)) {
            return null;
        }
        //? if =1.20.1 {
        /*CompoundTag root = weapon.getTag();
        if (root == null || !root.contains("genshinecraft.weapon_binding")) {
            return null;
        }
        CompoundTag tag = root.getCompound("genshinecraft.weapon_binding");
        try {
            return new WeaponBinding(
                    UUID.fromString(tag.getString("carrier_id")),
                    UUID.fromString(tag.getString("owner")),
                    CarrierData.Element.valueOf(tag.getString("element")),
                    tag.getString("carrier_name")
            );
        } catch (IllegalArgumentException exception) {
            return null;
        }
        *///?} else {
        return weapon.get(CarrierItems.weaponBindingType());
        //?}
    }

    /** Remove only Genshinecraft's binding; preserve enchantments, names and other item data. */
    public static boolean clear(ItemStack weapon) {
        //? if =1.20.1 {
        /*CompoundTag root = weapon.getTag();
        if (root == null || !root.contains("genshinecraft.weapon_binding")) {
            return false;
        }
        root.remove("genshinecraft.weapon_binding");
        if (root.isEmpty()) {
            weapon.setTag(null);
        }
        return true;
        *///?} else {
        return weapon.remove(CarrierItems.weaponBindingType()) != null;
        //?}
    }

    // The binding is a reference: a carrier taken away or abandoned cannot power the weapon.
    public static CarrierData resolve(ItemStack weapon, Player player) {
        return CarrierData.read(resolveStack(weapon, player));
    }

    public static ItemStack resolveStack(ItemStack weapon, Player player) {
        WeaponBinding binding = read(weapon);
        if (binding == null || !player.getUUID().equals(binding.owner())) {
            return ItemStack.EMPTY;
        }
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            CarrierData data = CarrierData.read(stack);
            if (data != null && data.id().equals(binding.carrierId())
                    && data.element() == binding.element() && data.usableBy(player.getUUID())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static void write(ItemStack weapon, WeaponBinding binding) {
        //? if =1.20.1 {
        /*CompoundTag tag = new CompoundTag();
        tag.putString("carrier_id", binding.carrierId().toString());
        tag.putString("owner", binding.owner().toString());
        tag.putString("element", binding.element().name());
        tag.putString("carrier_name", binding.carrierNameKey());
        weapon.getOrCreateTag().put("genshinecraft.weapon_binding", tag);
        *///?} else {
        weapon.set(CarrierItems.weaponBindingType(), binding);
        //?}
    }
}
