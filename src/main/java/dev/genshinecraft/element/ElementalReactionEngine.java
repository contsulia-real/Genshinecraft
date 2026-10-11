package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;

/** Pure, directional gauge reaction selection; call only after ICD permits a hit. */
public final class ElementalReactionEngine {
    public enum Reaction {
        NONE, VAPORIZE, MELT, OVERLOADED, SUPERCONDUCT, ELECTRO_CHARGED,
        FROZEN, SWIRL, CRYSTALLIZE, BURNING, BLOOM, QUICKEN, AGGRAVATE, SPREAD,
        BURGEON, HYPERBLOOM
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
        // The independent quicken state is not consumed by an Aggravate/Spread hit.
        if (state.isQuickened() && incoming == Element.ELECTRO) {
            return new Plan(Reaction.AGGRAVATE, Element.DENDRO, incoming, 0f, 1f);
        }
        if (state.isQuickened() && incoming == Element.DENDRO) {
            return new Plan(Reaction.SPREAD, Element.ELECTRO, incoming, 0f, 1f);
        }
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

    /** Existing is target's aura, incoming is the hit. Costs are in 1U equivalents. */
    public static Plan pair(Element existing, Element incoming) {
        if (pairIs(existing, incoming, Element.HYDRO, Element.PYRO)) {
            boolean strong = incoming == Element.HYDRO;
            return new Plan(Reaction.VAPORIZE, existing, incoming,
                    strong ? 2f : 0.5f, strong ? 2f : 1.5f);
        }
        if (pairIs(existing, incoming, Element.CRYO, Element.PYRO)) {
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
        if (pairIs(existing, incoming, Element.DENDRO, Element.PYRO)) {
            return new Plan(Reaction.BURNING, existing, incoming, 0.5f, 1f);
        }
        if (pairIs(existing, incoming, Element.DENDRO, Element.HYDRO)) {
            return new Plan(Reaction.BLOOM, existing, incoming, 0.5f, 1f);
        }
        if (pairIs(existing, incoming, Element.DENDRO, Element.ELECTRO)) {
            return new Plan(Reaction.QUICKEN, existing, incoming, 0.5f, 1f);
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
