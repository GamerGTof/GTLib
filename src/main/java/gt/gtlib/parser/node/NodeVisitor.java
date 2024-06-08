package gt.gtlib.parser.node;

import gt.gtlib.parser.node.*;

import java.util.function.Function;

public interface NodeVisitor<E> {
    E acceptNumberNode(NumberNode node);

    E acceptBooleanNode(BooleanNode node);

    E acceptObjectNode(ObjectNode node);

    E acceptStringNode(StringNode node);

    E acceptListNode(ListNode node);

    static<E> NodeVisitor<E> of(Function<NumberNode, E> numberNodeFunction, Function<BooleanNode, E> booleanNodeFunction, Function<ObjectNode,
            E> objectNodeFunction, Function<StringNode, E> stringNodeFunction, Function<ListNode, E> listNodeFunction) {
        return new NodeVisitor<>() {
            @Override
            public E acceptNumberNode(NumberNode node) {
                return numberNodeFunction.apply(node);
            }

            @Override
            public E acceptBooleanNode(BooleanNode node) {
                return booleanNodeFunction.apply(node);
            }

            @Override
            public E acceptObjectNode(ObjectNode node) {
                return objectNodeFunction.apply(node);
            }

            @Override
            public E acceptStringNode(StringNode node) {
                return stringNodeFunction.apply(node);
            }

            @Override
            public E acceptListNode(ListNode node) {
                return listNodeFunction.apply(node);
            }
        };
    }
}

