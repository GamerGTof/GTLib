package gt.gtlib.parser.node;

import java.util.function.Function;

public interface NumberNodeVisitor<T> {

    T acceptDoubleNumberNode(double num);

    T acceptShortNumberNode(short num);

    T acceptIntegerNumberNode(int num);

    T acceptFloatNumberNode(float num);

    T acceptByteNumberNode(byte num);

    T acceptLongNumberNode(long num);

    static<T> NumberNodeVisitor<T> of(Function<Double, T> doubleTFunction, Function<Short, T> shortTFunction, Function<Integer, T> integerTFunction,
                                   Function<Float, T> floatTFunction, Function<Byte, T> byteTFunction, Function<Long, T> longTFunction) {
        return new NumberNodeVisitor<>() {
            @Override
            public T acceptDoubleNumberNode(double num) {
                return doubleTFunction.apply(num);
            }

            @Override
            public T acceptShortNumberNode(short num) {
                return shortTFunction.apply(num);
            }

            @Override
            public T acceptIntegerNumberNode(int num) {
                return integerTFunction.apply(num);
            }

            @Override
            public T acceptFloatNumberNode(float num) {
                return floatTFunction.apply(num);
            }

            @Override
            public T acceptByteNumberNode(byte num) {
                return byteTFunction.apply(num);
            }

            @Override
            public T acceptLongNumberNode(long num) {
                return longTFunction.apply(num);
            }
        };
    }
}
