package gt.gtlib.parser.translation;

import gt.gtlib.parser.node.Node;

/**
 * Unlike {@link gt.gtlib.parser.serialiser.Serializer} and {@link gt.gtlib.parser.deserialiser.Deserializer}, that converts from and to string, this class make the translation from an object to its node representation and vice-versa.
 *
 * @param <T> The object it should translate from/to.
 */
public interface Translator<T> {
    /**
     * @param root The node representation that matches {@link T}.
     * @return An instance of {@link T} matching its node representation {@param root}.
     */
    T translateTo(Node<?> root);

    /**
     * @param root The object this translator translates.
     * @return The node representation of {@link T}.
     */
    Node<?> translateFrom(T root);
}
