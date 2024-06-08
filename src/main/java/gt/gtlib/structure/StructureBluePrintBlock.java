package gt.gtlib.structure;

import org.bukkit.util.Vector;

public class StructureBluePrintBlock {
    private final Vector position;
    private final StructureBlockData blockData;

    public StructureBluePrintBlock(Vector position, StructureBlockData blockData) {
        this.position = position;
        this.blockData = blockData;
    }
}