package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData;
import java.util.Set;

/** Bit order matches CarrierData.Element.values() and the attached_elements atlas. */
public final class AuraStatusIcons {
    private AuraStatusIcons() {}

    public static int mask(Set<CarrierData.Element> elements) {
        int result = 0;
        for (CarrierData.Element element : elements) {
            result |= 1 << element.ordinal();
        }
        return result;
    }
}
