package gt.gtlib.parser.node;

import gt.gtlib.parser.common.NodeStream;
import gt.gtlib.parser.translation.ObjectTranslator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Base class for all node types. In which are included {@link ObjectNode},{@link NumberNode},{@link StringNode}, {@link ListNode} and {@link BooleanNode}.
 *
 * @param <T> The type of the value this node holds.
 */
public interface Node<T> {

    /**
     * @return {@link T}, the value this node holds.
     */
    T getValue();

    /**
     * @return The type of this node. See {@link NodeType}.
     */
    NodeType getType();

    /**
     * Method to support visitor pattern.
     *
     * @param visitor Class that implements NodeVisitor.
     * @param <E>     Generic to relate the return type with the node visitor return type.
     * @return {@link E} the visitor's return type.
     */
    <E> E accept(NodeVisitor<E> visitor);

    /**
     * Stream all possible sub-nodes in no particular order.
     *
     * @return Stream of nodes.
     */
    default Stream<Node<?>> stream() {
        return NodeStream.stream(this);
    }

    static Node<?> of(@Nullable Object target) {
        return  ObjectTranslator.getDefault().translateFrom(target);
    }

    static ObjectNode of(@NotNull Map<Node<?>, Node<?>> map) {
        return ObjectNode.of(map);
    }

    static ListNode of(@NotNull List<Node<?>> list) {
        return ListNode.of(list);
    }

    static StringNode of(@NotNull String string) {
        return StringNode.of(string);
    }

    static BooleanNode of(@NotNull Boolean bool) {
        return BooleanNode.of(bool);
    }

    static BooleanNode of(boolean bool) {
        return of(Boolean.valueOf(bool));
    }


    static NumberNode of(double num) {
        return NumberNode.of(Double.valueOf(num));
    }

    static NumberNode of(byte num) {
        return NumberNode.of(Byte.valueOf(num));
    }

    static NumberNode of(short num) {
        return NumberNode.of(Short.valueOf(num));
    }

    static NumberNode of(int num) {
        return NumberNode.of(Integer.valueOf(num));
    }

    static NumberNode of(long num) {
        return NumberNode.of(Long.valueOf(num));
    }

    static NumberNode of(float num) {
        return NumberNode.of(Float.valueOf(num));
    }
}
