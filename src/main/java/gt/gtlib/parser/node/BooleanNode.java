package gt.gtlib.parser.node;

import java.util.Objects;

public class BooleanNode implements ScalarNode<Boolean> {
    private final Boolean value;

    protected BooleanNode(Boolean value) {
        this.value = value;
    }

    public static BooleanNode of(Boolean value) {
        return new BooleanNode(value);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public NodeType getType() {
        return NodeType.BOOLEAN;
    }

    @Override
    public <E> E accept(NodeVisitor<E> visitor) {
        return visitor.acceptBooleanNode(this);
    }

    @Override
    public String toString() {
        return "BooleanNode{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanNode that = (BooleanNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
