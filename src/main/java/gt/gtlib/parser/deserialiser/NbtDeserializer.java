package gt.gtlib.parser.deserialiser;

import gt.gtlib.parser.common.NbtDataType;
import gt.gtlib.parser.node.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

public class NbtDeserializer implements Deserializer {

    private ByteBuffer src;

    @Override
    public Node<?> deserialize(InputStream inputStream) {
        final var buffedStream = new BufferedInputStream(inputStream);
        final ByteBuffer buffer;
        if (isGZipped(buffedStream)) {
            try (var stream = new GZIPInputStream(buffedStream)) {
                buffer = ByteBuffer.wrap(stream.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException("Could not decompress gzipped stream.", e);
            }
        } else {
            try {
                buffer = ByteBuffer.wrap(buffedStream.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.src = buffer;
        return getRoot();
    }

    private boolean isGZipped(BufferedInputStream stream) {
        stream.mark(1);
        final boolean res;
        try {
            res = stream.read() != NbtDataType.COMPOUND_TAG_START.getId();
            stream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    Node<?> getRoot() {
        return Node.of(Map.ofEntries(compoundTagEntry()));
    }


    Node<?> getFromId(int id) {
        return switch (NbtDataType.fromId(id)) {
            case COMPOUND_TAG_START -> compoundTag();
            case BYTE_ARRAY_TAG -> byteArrayTag();
            case LONG_ARRAY_TAG -> longArrayTag();
            case INT_ARRAY_TAG -> intArrayTag();
            case DOUBLE_TAG -> doubleTag();
            case STRING_TAG -> stringTag();
            case FLOAT_TAG -> floatTag();
            case SHORT_TAG -> shortTag();
            case LONG_TAG -> longTag();
            case BYTE_TAG -> byteTag();
            case LIST_TAG -> listTag();
            case INT_TAG -> intTag();
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    private ListNode longArrayTag() {
        return listOfNative(src::getLong);
    }

    private ListNode intArrayTag() {
        return listOfNative(src::getInt);
    }

    private ListNode byteArrayTag() {
        return listOfNative(src::get);
    }

    private ListNode listOfNative(Supplier<Number> numberSupplier) {
        final int len = src.getInt();
        final Node<?>[] res = new Node<?>[len];
        for (int i = 0; i < res.length; i++) {
            res[i] = NumberNode.of(numberSupplier.get());
        }
        return ListNode.of(List.of(res));
    }

    private Node<?> listTag() {
        final int id = id();
        final int length = src.getInt();
        final List<Node<?>> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(getFromId(id));
        }
        return Node.of(list);
    }

    private NumberNode intTag() {
        return Node.of(src.getInt());
    }

    private NumberNode floatTag() {
        return Node.of(src.getFloat());

    }

    private NumberNode doubleTag() {
        return Node.of(src.getDouble());
    }

    private NumberNode byteTag() {
        return Node.of(src.get());
    }


    @SuppressWarnings({"unchecked","rawtypes"})
    private ObjectNode compoundTag() {
        final ArrayList<Map.Entry<Node<?>, Node<?>>> entries = new ArrayList<>();

        while (src.get(src.position()) != NbtDataType.COMPOUND_TAG_END.getId()) {
            final var tag = compoundTagEntry();
            entries.add(tag);
        }
        id(); // Advance the COMPOUND_TAG_END byte.

        return ObjectNode.of(Map.ofEntries(entries.toArray(Map.Entry[]::new)));
    }

    private StringNode stringTag() {
        final short len = src.getShort();

        final byte[] raw = new byte[len];
        src.get(raw);
        return Node.of(new String(raw, StandardCharsets.UTF_8));
    }

    private Map.Entry<Node<?>, Node<?>> compoundTagEntry() {
        final int elementId = id();
        return Map.entry(stringTag(), getFromId(elementId));
    }

    private NumberNode longTag() {
        return Node.of(src.getLong());
    }

    private NumberNode shortTag() {
        return Node.of(src.getShort());
    }


    private int id() {
        return src.get();
    }

}
