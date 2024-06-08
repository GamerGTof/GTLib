package gt.gtlib.parser.serialiser;

import gt.gtlib.parser.common.NbtDataType;
import gt.gtlib.parser.node.*;
import gt.gtlib.parser.node.NodeVisitor;
import gt.gtlib.parser.node.NumberNodeVisitor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NbtSerializer implements Serializer {
    @Override
    public void serialise(OutputStream outputStream, Node<?> ast) {
        new NbtWritter(outputStream, ast);
    }

    private static final class NbtWritter implements NodeVisitor<Void> {

        private final OutputStream outputStream;
        private NbtDataType currentType;

        public NbtWritter(OutputStream outputStream, Node<?> root) {
            this.outputStream = outputStream;

            writeRoot(root);
        }

        private void writeRoot(Node<?> root) {
            if (!(root instanceof ObjectNode objectNode)) {
                throw new IllegalArgumentException("Nbt format states all nodes is inside a hidden object node.");
            }
            final var underlyingEntrySet = objectNode.getValue().entrySet();
            if (underlyingEntrySet.size() > 1) {
                throw new IllegalArgumentException("Cannot have more than one node at top level.");
            }
            underlyingEntrySet.forEach(this::compoundEntry);
        }


        @Override
        public Void acceptNumberNode(NumberNode node) {
            final var value = node.getValue();
            final ByteBuffer buffer = switch (currentType) {
                case DOUBLE_TAG -> ByteBuffer.allocate(Double.BYTES).putDouble((Double) value);
                case INT_TAG -> ByteBuffer.allocate(Integer.BYTES).putInt((Integer) value);
                case FLOAT_TAG -> ByteBuffer.allocate(Float.BYTES).putFloat((Float) value);
                case SHORT_TAG -> ByteBuffer.allocate(Short.BYTES).putShort((Short) value);
                case LONG_TAG -> ByteBuffer.allocate(Long.BYTES).putLong((Long) value);
                case BYTE_TAG -> ByteBuffer.allocate(Byte.BYTES).put((Byte) value);
                default -> throw new IllegalArgumentException("Unexpected number in NumberNode: " + value);
            };
            write(buffer);
            return null;
        }

        @Override
        public Void acceptBooleanNode(BooleanNode node) {
            final var buffer = ByteBuffer.allocate(Byte.BYTES).put((byte) (node.getValue() ? 1 : 0));
            write(buffer);
            return null;
        }

        @Override
        public Void acceptObjectNode(ObjectNode node) {
            node.getValue().entrySet().forEach(this::compoundEntry);
            write(ByteBuffer.allocateDirect(Byte.BYTES).put((byte) NbtDataType.COMPOUND_TAG_END.getId()));
            return null;
        }

        @Override
        public Void acceptStringNode(StringNode node) {
            writeStr(node.getValue());
            return null;
        }

        @Override
        public Void acceptListNode(ListNode node) {
            final var list = node.getValue();
            final NbtDataType elementsType;
            if (list.isEmpty()) {
                elementsType = NbtDataType.COMPOUND_TAG_END;
            } else {
                final var element = list.get(0);
                final var elementType = element.getType();
                if (!list.stream().allMatch(e -> e.getType() == elementType)) {
                    throw new IllegalStateException("All elements must be of same type in List: " + node);
                }
                elementsType = getNodeNbtType(element);
            }
            write(ByteBuffer.allocateDirect(Byte.BYTES).put((byte) elementsType.getId()).putInt(list.size()));
            list.forEach(element -> element.accept(this));
            return null;
        }

        private void write(ByteBuffer buffer) {
            final byte[] buff = new byte[buffer.limit()];
            buffer.get(0, buff, 0, buff.length);
            write(buff);
        }

        private void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void writeStr(String string) {
            final var valueBytes = string.getBytes(StandardCharsets.UTF_8);
            final var buffer = ByteBuffer.allocate(Short.BYTES + (Byte.BYTES * valueBytes.length)).putShort((short) valueBytes.length).put(valueBytes);
            write(buffer);
        }


        private void compoundEntry(Map.Entry<Node<?>, Node<?>> entry) {
            final var value = entry.getValue();
            final var valueType = getNodeNbtType(value);
            write(ByteBuffer.allocate(Byte.BYTES).put((byte) valueType.getId()));
            writeStr(entry.getKey().getValue().toString());

            currentType = valueType;
            value.accept(this);
        }

        private NbtDataType getNodeNbtType(Node<?> node) {
            return node.accept(NBT_DATA_TYPE_NODE_VISITOR);
        }

        /**
         * As to write bytes to the stream the number type is important, this method uses the visitor class to get the number node type converted to nbt number type.
         */
        private NbtDataType getNumberNbtType(NumberNode number) {
            return number.accept(NBT_DATA_TYPE_NUMBER_NODE_VISITOR);
        }


        private final NodeVisitor<NbtDataType> NBT_DATA_TYPE_NODE_VISITOR = NodeVisitor.of(
                numberNode -> this.getNumberNbtType(numberNode),
                booleanNode -> NbtDataType.BYTE_TAG,
                objectNode -> NbtDataType.COMPOUND_TAG_START,
                stringNode -> NbtDataType.STRING_TAG,
                listNode -> NbtDataType.LIST_TAG
        );
        private final NumberNodeVisitor<NbtDataType> NBT_DATA_TYPE_NUMBER_NODE_VISITOR = NumberNodeVisitor.of(
                doubleNode -> NbtDataType.DOUBLE_TAG,
                shortNode -> NbtDataType.SHORT_TAG,
                integerNode -> NbtDataType.INT_TAG,
                floatNode -> NbtDataType.FLOAT_TAG,
                byteNode -> NbtDataType.BYTE_TAG,
                longNode -> NbtDataType.LONG_TAG
        );

    }


}
