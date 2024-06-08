package gt.gtlib.parser.deserialiser;

import gt.gtlib.parser.node.Node;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public interface Deserializer {
    /**
     * Deserializes the {@link String} of the current format, into a node tree structure.
     * Keep in mind if the format is in binary, such as nbt, the {@link String} object is not the same as the raw bytes.
     *
     * @param src The source string.
     * @return Node representation of the parsed format.
     */
    default Node<?> deserialize(String src) {
        return deserialize(src.getBytes());
    }

    /**
     * This method, in implementations where the format isn't binary, will work just like its String overload.
     *
     * @param raw Raw bytes to be parsed.
     * @return The node representation of the parsed format.
     */
    default Node<?> deserialize(byte[] raw) {
        return deserialize(new ByteArrayInputStream(raw));
    }

    /**
     * Parse the format given an {@link InputStream}.
     *
     * @param inputStream The stream to be read.
     * @return The node representation of the parsed format.
     */
    default Node<?> deserialize(InputStream inputStream) {
        final var writer = new StringWriter();
        try {
            new InputStreamReader(inputStream).transferTo(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deserialize(writer.toString());
    }
}
