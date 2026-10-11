package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;

/**
 * Gauge-driven reaction selection. Reading a plan has no side effects: it is
 * safe before vanilla damage calculation and committed only after a real hit.
 */
public final class ElementalReactionEngine {
    public enum Reaction {
        NONE, VAPORIZE, MELT, OVERLOADED, SUPERCONDUCT, ELECTRO_CHARGED,
        FROZEN, SWIRL, CRYSTALLIZE
    }

    public record Plan(Reaction reaction, Element existing, Element incoming,
                       float gaugeCost, float damageMultiplier) {
        public static Plan none(Element incoming) {
            return new Plan(Reaction.NONE, null, incoming, 0f, 1f);
        }
    }

    private ElementalReactionEngine() {
    }

    public static Plan preview(ElementalAuraState state, Element incoming) {
        for (Element existing : Element.values()) {
            if (existing == incoming || !state.has(existing)) {
                continue;
            }
            Plan plan = pair(existing, incoming);
            if (plan.reaction() != Reaction.NONE) {
                return plan;
            }
        }
        return Plan.none(incoming);
    }

    /**
     * The order matters: existing is the target's aura; incoming is the hit.
     * Gauge costs are in units of the incoming 1U application.
     */
    public static Plan pair(Element existing, Element incoming) {
        if ((existing == Element.HYDRO && incoming == Element.PYRO)
                || (existing == Element.PYRO && incoming == Element.HYDRO)) {
            boolean strong = incoming == Element.HYDRO;
            return new Plan(Reaction.VAPORIZE, existing, incoming,
                    strong ? 2f : 0.5f, strong ? 2f : 1.5f);
        }
        if ((existing == Element.CRYO && incoming == Element.PYRO)
                || (existing == Element.PYRO && incoming == Element.CRYO)) {
            boolean strong = incoming == Element.PYRO;
            return new Plan(Reaction.MELT, existing, incoming,
                    strong ? 2f : 0.5f, strong ? 2f : 1.5f);
        }
        if (pairIs(existing, incoming, Element.PYRO, Element.ELECTRO)) {
            return new Plan(Reaction.OVERLOADED, existing, incoming, 1f, 1f);
        }
        if (pairIs(existing, incoming, Element.CRYO, Element.ELECTRO)) {
            return new Plan(Reaction.SUPERCONDUCT, existing, incoming, 0.5f, 1f);
        }
        if (pairIs(existing, incoming, Element.HYDRO, Element.ELECTRO)) {
            return new Plan(Reaction.ELECTRO_CHARGED, existing, incoming, 0f, 1f);
        }
        if (pairIs(existing, incoming, Element.HYDRO, Element.CRYO)) {
            return new Plan(Reaction.FROZEN, existing, incoming, 1f, 1f);
        }
        if (incoming == Element.ANEMO && dispersible(existing)) {
            return new Plan(Reaction.SWIRL, existing, incoming, 0.5f, 1f);
        }
        if (incoming == Element.GEO && dispersible(existing)) {
            return new Plan(Reaction.CRYSTALLIZE, existing, incoming, 0.5f, 1f);
        }
        return Plan.none(incoming);
    }

    private static boolean pairIs(Element a, Element b, Element first, Element second) {
        return (a == first && b == second) || (a == second && b == first);
    }

    private static boolean dispersible(Element element) {
        return element == Element.PYRO || element == Element.HYDRO
                || element == Element.ELECTRO || element == Element.CRYO;
    }
}
