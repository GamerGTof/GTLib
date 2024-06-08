package gt.gtlib.parser.node;

import java.util.Objects;
import java.util.function.BiFunction;

public abstract class NumberNode implements ScalarNode<Number> {
    private final Number value;

    protected NumberNode(Number value) {
        this.value = value;
    }


    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public NodeType getType() {
        return NodeType.NUMBER;
    }

    public abstract <E> E accept(NumberNodeVisitor<E> visitor);

    @Override
    public <E> E accept(NodeVisitor<E> visitor) {
        return visitor.acceptNumberNode(this);
    }

    @Override
    public String toString() {
        return "NumberNode{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberNode that = (NumberNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public static NumberNode of(Number num) {
        if (num instanceof Integer value) {
            return of(value);
        } else if (num instanceof Double value) {
            return of(value);
        } else if (num instanceof Float value) {
            return of(value);
        } else if (num instanceof Byte value) {
            return of(value);
        } else if (num instanceof Short value) {
            return of(value);
        } else if (num instanceof Long value) {
            return of(value);
        }
        throw new IllegalArgumentException();
    }

    public static NumberNode of(int num) {
        return of(Integer.valueOf(num));
    }

    public static NumberNode of(double num) {
        return of(Double.valueOf(num));
    }

    public static NumberNode of(short num) {
        return of(Short.valueOf(num));
    }

    public static NumberNode of(float num) {
        return of(Float.valueOf(num));
    }

    public static NumberNode of(long num) {
        return of(Long.valueOf(num));
    }

    public static NumberNode of(byte num) {
        return of(Byte.valueOf(num));
    }


    public static NumberNode of(Short num) {
        return ofTyped(num, NumberNodeVisitor::acceptShortNumberNode);
    }

    public static NumberNode of(Float num) {
        return ofTyped(num, NumberNodeVisitor::acceptFloatNumberNode);
    }

    public static NumberNode of(Long num) {
        return ofTyped(num, NumberNodeVisitor::acceptLongNumberNode);
    }

    public static NumberNode of(Byte num) {
        return ofTyped(num, NumberNodeVisitor::acceptByteNumberNode);
    }

    public static NumberNode of(Integer num) {
        return ofTyped(num, NumberNodeVisitor::acceptIntegerNumberNode);
    }

    public static NumberNode of(Double num) {
        return ofTyped(num, NumberNodeVisitor::acceptDoubleNumberNode);
    }


    @SuppressWarnings("unchecked")
    private static <N extends Number, T> NumberNode ofTyped(N num, BiFunction<NumberNodeVisitor<T>, N, T> call) {
        return new NumberNode(num) {
            @Override
            public <E> E accept(NumberNodeVisitor<E> visitor) {
                return (E) call.apply((NumberNodeVisitor<T>) visitor, num);
            }
        };
    }

}
