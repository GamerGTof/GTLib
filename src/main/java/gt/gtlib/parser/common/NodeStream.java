package gt.gtlib.parser.common;

import gt.gtlib.parser.node.*;
import gt.gtlib.parser.node.NodeVisitor;

import java.util.stream.*;

public final class NodeStream implements NodeVisitor<Stream<Node<?>>> {
    private static final NodeStream INSTANCE = new NodeStream();


    private NodeStream() {
    }

    public static Stream<Node<?>> stream(Node<?> node) {
        return node.accept(INSTANCE);
    }

    @Override
    public Stream<Node<?>> acceptNumberNode(NumberNode node) {
        return Stream.of(node);
    }

    @Override
    public Stream<Node<?>> acceptBooleanNode(BooleanNode node) {
        return Stream.of(node);
    }

    @Override
    public Stream<Node<?>> acceptObjectNode(ObjectNode node) {
        return node.getValue().entrySet().stream().flatMap(e -> Stream.concat(
                e.getKey().accept(this), e.getValue().accept(this)
        ));
    }

    @Override
    public Stream<Node<?>> acceptStringNode(StringNode node) {
        return Stream.of(node);
    }

    @Override
    public Stream<Node<?>> acceptListNode(ListNode node) {
        return node.getValue().stream().flatMap(n -> n.accept(this));
    }

}
