package gt.gtlib.structure;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

public interface StructureBlockData {

    void applyTo(Block block);

    default StructureBlockData of(BlockState state) {
        final var savedData = state.getBlockData();
        return of(savedData);
    }

    default StructureBlockData of(Block block) {
        final var savedData = block.getBlockData();
        return of(savedData);
    }

    static StructureBlockData of(BlockData blockData) {
        return b -> b.setBlockData(blockData);
    }
}
