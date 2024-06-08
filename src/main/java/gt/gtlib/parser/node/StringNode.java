package gt.gtlib.parser.node;

import java.util.Objects;

public class StringNode implements ScalarNode<String> {
    private final String value;

    protected StringNode(String value) {
        this.value = value;
    }

    public static StringNode of(String string) {
        return new StringNode(string);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public NodeType getType() {
        return NodeType.STRING;
    }

    @Override
    public <E> E accept(NodeVisitor<E> visitor) {
        return visitor.acceptStringNode(this);
    }

    @Override
    public String toString() {
        return "StringNode{" +
                "value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringNode that = (StringNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
