package gt.gtlib.parser.node;

/**
 * This class is used as a base class for all implementation that holds a scalar value.
 * Being not considered scalar value: {@link ListNode} and {@link ObjectNode}. All others are considered.
 * @param <T> The scalar value being held.
 */
public interface ScalarNode<T> extends Node<T>{
}
