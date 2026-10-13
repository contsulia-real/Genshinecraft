package dev.genshinecraft.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshots actual blocks belonging to the currently loaded statue's bounding volume,
 * in structure-relative coordinates. These are real block states, not painted pixels.
 */
public final class StatuePreviewScene {
    public record Part(BlockState state, int x, int y, int z) {}
    public record Snapshot(List<Part> parts) {}

    private StatuePreviewScene() {}

    public static Snapshot capture(ClientLevel world, BlockPos altar) {
        // Statue structure template: 17 x 19 x 17, altar at template (8,3,4).
        // The sea lantern at (8,11,8) determines how the structure was rotated.
        int rotation = 0;
        for (int i = 0; i < 4; i++) {
            int dx = i == 1 ? 4 : i == 3 ? -4 : 0;
            int dz = i == 0 ? 4 : i == 2 ? -4 : 0;
            if (world.getBlockState(altar.offset(dx, 8, dz)).is(Blocks.SEA_LANTERN)) {
                rotation = i;
                break;
            }
        }

        List<Part> visible = new ArrayList<>();
        for (int y = -3; y <= 15; y++) {
            for (int z = -4; z <= 12; z++) {
                for (int x = -8; x <= 8; x++) {
                    BlockPos pos = rotated(altar, x, y, z, rotation);
                    BlockState state = world.getBlockState(pos);
                    if (state.isAir() || state.getRenderShape() != RenderShape.MODEL) continue;
                    // Keep all actual exposed geometry; suppress buried full blocks.
                    if (isOccluding(world.getBlockState(pos.above()))
                            && isOccluding(world.getBlockState(rotated(altar, x + 1, y, z, rotation)))
                            && isOccluding(world.getBlockState(rotated(altar, x - 1, y, z, rotation)))
                            && isOccluding(world.getBlockState(rotated(altar, x, y, z + 1, rotation)))
                            && isOccluding(world.getBlockState(rotated(altar, x, y, z - 1, rotation)))) {
                        continue;
                    }
                    visible.add(new Part(state, x, y, z));
                }
            }
        }
        return new Snapshot(List.copyOf(visible));
    }

    private static boolean isOccluding(BlockState state) {
        return state.canOcclude() && state.isCollisionShapeFullBlock(net.minecraft.world.level.EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    private static BlockPos rotated(BlockPos origin, int x, int y, int z, int rotation) {
        return switch (rotation) {
            case 1 -> origin.offset(z, y, -x);
            case 2 -> origin.offset(-x, y, -z);
            case 3 -> origin.offset(-z, y, x);
            default -> origin.offset(x, y, z);
        };
    }
}
