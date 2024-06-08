package gt.gtlib.parser.translation;

import gt.gtlib.parser.common.ObjectMapper;
import gt.gtlib.parser.node.*;
import gt.gtlib.utils.Reflections;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectTranslator implements Translator<Object> {

    private final Map<Class<?>, ObjectMapper> objectMapping = new HashMap<>();
    private final Map<NodeTypeBlob, Class<?>> classLookup = new HashMap<>();

    private final Function<Class<?>, ObjectMapper> mapperSupplier;

    private static final ObjectTranslator DEFAULT = new ObjectTranslator();

    /**
     * Creates a new instance using the default {@link ObjectMapper}.
     */
    public ObjectTranslator() {
        this(ObjectMapper::defaultMapper);
    }

    /**
     *
     * @param mapperSupplier Function that'll be called each time a class (object) needs an mapping.
     */
    public ObjectTranslator(Function<Class<?>, ObjectMapper> mapperSupplier) {
        this.mapperSupplier = mapperSupplier;
    }

    public static ObjectTranslator getDefault() {
        return DEFAULT;
    }


    /**
     * This method will only be able to return an instance if {@link #translateFrom(Object)} was called before with the same object type.
     *
     * @param root The node representation of an object.
     * @return The object instance which matches the node representation.
     * @throws IllegalStateException If no mapping was found.
     */
    @Override
    public Object translateTo(Node<?> root) {
        final var foundType = classLookup.get(new NodeTypeBlob(root));
        if (foundType == null) {
            throw new IllegalStateException("No mapping for node structure.");
        }
        return objectMapping.get(foundType)
                .setMapping(root, Reflections.newInstanceOf(foundType));
    }


    /**
     * This method maps the object for future calls and relate its class to the node structure generated, for later calls to {@link  #translateTo(Node)}.
     *
     * @param src The object this translator translates.
     * @return The node representation of the object.
     */
    @Override
    public Node<?> translateFrom(Object src) {
        final var type = src.getClass();
        final var mapping = objectMapping.computeIfAbsent(type, mapperSupplier);
        final var struct = mapping.getMapping(src);

        classLookup.computeIfAbsent(new NodeTypeBlob(struct), blob -> type);
        return struct;
    }


    private static class NodeTypeBlob {
        private final List<NodeType> types;
        private final int hash;

        public NodeTypeBlob(Node<?> root) {
            this.types = root.stream().map(Node::getType).toList();
            hash = Objects.hash(types);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeTypeBlob that = (NodeTypeBlob) o;
            return Objects.equals(types, that.types);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }


}
