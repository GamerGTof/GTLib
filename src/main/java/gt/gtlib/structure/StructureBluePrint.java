package gt.gtlib.structure;

import gt.gtlib.utils.Vectors;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StructureBluePrint {
    private static final StructureBluePrintBlock[][][] EMPTY_BLOCKS = new StructureBluePrintBlock[0][0][0];
    private final StructureBluePrintBlock[][][] blocks;

    protected StructureBluePrint(StructureBluePrintBlock[][][] blocks) {
        this.blocks = blocks;
    }


    public static StructureBluePrint ofUnsorted(@NotNull List<Block> blocks) {
        if (blocks.isEmpty()) {
            return new StructureBluePrint(EMPTY_BLOCKS);
        }
        final var len = blocks.size();
        Vector min = null;
        double maxX = 0;
        double maxY = 0;
        double maxZ = 0;
        final List<Vector> vectors = new ArrayList<>(len);
        for (final var block : blocks) {
            final var vector = block.getLocation().toVector();
            vectors.add(vector);
            maxX = Math.max(vector.getX(), maxX);
            maxY = Math.max(vector.getY(), maxY);
            maxZ = Math.max(vector.getZ(), maxZ);
            if (min == null) {
                min = vector;
            } else if (Vectors.smallerThan(vector, min)) {
                min = vector;
            }
        }
        final var structBlocks = new StructureBluePrintBlock[(int) maxX][(int) maxY][(int) maxZ];
        for (int i = 0; i < len; i++) {
            final var vector = vectors.get(i).subtract(min);
            final var block = blocks.get(i);
            structBlocks[vector.getBlockX()][vector.getBlockY()][vector.getBlockZ()] = new StructureBluePrintBlock(vector, StructureBlockData.of(block.getBlockData()));
        }
        return new StructureBluePrint(structBlocks);
    }

    public static StructureBluePrint ofUnsorted(Block[] blocks) {
        return ofUnsorted(Arrays.asList(blocks));
    }

    public void buildAt(Location location){

    }

}