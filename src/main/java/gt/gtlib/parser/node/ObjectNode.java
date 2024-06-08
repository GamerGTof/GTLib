package gt.gtlib.parser.node;


import java.util.Map;
import java.util.Objects;

public class ObjectNode implements Node<Map<Node<?>, Node<?>>> {
    private final Map<Node<?>, Node<?>> values;

    protected ObjectNode(
            Map<Node<?>, Node<?>> values) {
        this.values = values;
    }

    @Override
    public Map<Node<?>, Node<?>> getValue() {
        return values;
    }

    @Override
    public NodeType getType() {
        return NodeType.OBJECT;
    }

    @Override
    public <E> E accept(NodeVisitor<E> visitor) {
        return visitor.acceptObjectNode(this);
    }

    @Override
    public String toString() {
        return "ObjectNode{" +
                "values=" + values +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectNode that = (ObjectNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    public static ObjectNode of(Map<Node<?>, Node<?>> map) {
        return new ObjectNode(map);
    }
}