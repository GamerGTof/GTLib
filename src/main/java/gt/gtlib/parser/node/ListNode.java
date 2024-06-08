package gt.gtlib.parser.node;

import java.util.List;
import java.util.Objects;

public class ListNode implements Node<List<Node<?>>> {
    private final List<Node<?>> values;

    protected ListNode(List<Node<?>> nodes) {
        this.values = nodes;
    }

    public static ListNode of(List<Node<?>> list) {
        return new ListNode(list);
    }

    @Override
    public List<Node<?>> getValue() {
        return values;
    }

    @Override
    public NodeType getType() {
        return NodeType.LIST;
    }

    @Override
    public <E> E accept(NodeVisitor<E> visitor) {
        return visitor.acceptListNode(this);
    }

    @Override
    public String toString() {
        return "ListNode{" +
                "values=" + values +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListNode listNode = (ListNode) o;
        return Objects.equals(values, listNode.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}