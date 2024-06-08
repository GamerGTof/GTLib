package gt.gtlib.parser.serialiser;

import gt.gtlib.parser.node.Node;

import java.io.*;
import java.nio.ByteBuffer;

public interface Serializer {
    default String serialise(Node<?> ast) {
        final var bArray = new ByteArrayOutputStream();
        serialise(bArray, ast);
        return bArray.toString();
    }

    default void serialise(OutputStream outputStream, Node<?> ast) {
        try {
            new OutputStreamWriter(outputStream).write(serialise(ast));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
