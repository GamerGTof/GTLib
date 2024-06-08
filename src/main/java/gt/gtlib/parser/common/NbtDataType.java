package gt.gtlib.parser.common;

import java.util.Arrays;
import java.util.Comparator;

public enum NbtDataType {
    LONG_ARRAY_TAG(12),
    INT_ARRAY_TAG(11),
    COMPOUND_TAG_START(10),
    LIST_TAG(9),
    STRING_TAG(8),
    BYTE_ARRAY_TAG(7),
    DOUBLE_TAG(6),
    FLOAT_TAG(5),
    LONG_TAG(4),
    INT_TAG(3),
    SHORT_TAG(2),
    BYTE_TAG(1),
    COMPOUND_TAG_END(0);


    private static final NbtDataType[] LOOKUP_TABLE = Arrays.stream(NbtDataType.values())
            .sorted(Comparator.comparingInt(NbtDataType::getId))
            .toArray(NbtDataType[]::new);

    public static NbtDataType fromId(int id) {
        if (id < 0 || id > LOOKUP_TABLE.length) {
            throw new IllegalArgumentException("Unexpected id value: " + id);
        }

        return LOOKUP_TABLE[id];
    }

    private final int id;

    NbtDataType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
