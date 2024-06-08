package gt.gtlib.parser.common;

import gt.gtlib.parser.node.*;
import gt.gtlib.utils.Reflections;
import gt.gtlib.utils.data.Pair;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectMapper {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final Map<Pair<StringNode, VarHandle>, Value> mapping;

    public ObjectMapper(Class<?> c) {
        this.mapping = map(c);
    }

    public static ObjectMapper defaultMapper(Class<?> c) {
        return new ObjectMapper(c);
    }

    private Map<Pair<StringNode, VarHandle>, ObjectMapper.Value> map(Class<?> type) {
        final var fields = Reflections.getFields(type);
        final Map<Pair<StringNode, VarHandle>, ObjectMapper.Value> map = new HashMap<>(fields.size());
        try {
            final var lookUpIn = MethodHandles.privateLookupIn(type, LOOKUP);
            for (final var field : fields) {
                final var key = Pair.of(
                        StringNode.of(field.getName()),
                        lookUpIn.unreflectVarHandle(field)
                );
                final ObjectMapper.Value value = getValueFor(field.getType());

                map.put(key, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    private ObjectMapper.Value getValueFor(Class<?> type) {
        if (type.isAssignableFrom(String.class)) {
            return o -> Node.of((String) o);
        }
        if (type.isAssignableFrom(Boolean.TYPE)) {
            return o -> Node.of((Boolean) o);
        }
        if (type.isAssignableFrom(Number.class) || type.isPrimitive()) {
            return getValueNumber(type);
        }
        if (type.isAssignableFrom(Collection.class)) {
            return getValueCollection(type);
        }

        final var mapper = new ObjectMapper(type);
        return Value.of(mapper::getMapping, ((node, o, field) -> {
            final var val = Reflections.getFieldValue(field, o);
            final var obj = (val == null)
                    ? Reflections.newInstanceOf(type)
                    : val;
            return mapper.setMapping(node, obj);
        }));
    }

    private ObjectMapper.Value getValueCollection(Class<?> type) {
        final var elementType = (Class<?>) Reflections.getClassParameterizedType(type, 0);
        final ObjectMapper.Value elementValue = elementType.equals(Object.class)
                ? Node::of // If type is object, then the list is dynamic.
                : getValueFor(elementType);

        return o -> {
            final var collection = (Collection<?>) o;
            return ListNode.of(collection.stream().map(elementValue::getMapping).collect(Collectors.toUnmodifiableList()));
        };
    }

    private ObjectMapper.Value getValueNumber(Class<?> type) {
        if (type.equals(Integer.TYPE)) {
            return o -> NumberNode.of((Integer) o);
        } else if (type.equals(Double.TYPE)) {
            return o -> NumberNode.of((Double) o);
        } else if (type.equals(Float.TYPE)) {
            return o -> NumberNode.of((Float) o);
        } else if (type.equals(Byte.TYPE)) {
            return o -> NumberNode.of((Byte) o);
        } else if (type.equals(Short.TYPE)) {
            return o -> NumberNode.of((Short) o);
        } else if (type.equals(Long.TYPE)) {
            return o -> NumberNode.of((Long) o);
        }
        throw new RuntimeException("Invalid Number class");
    }

    public Node<?> getMapping(Object o) {
        return ObjectNode.of(mapping.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                v -> v.getKey().k(),
                v -> v.getValue().getMapping(
                        v.getKey().v().get(o)
                )
        )));
    }

    public Object setMapping(Node<?> node, Object o) {
        if (!(node instanceof ObjectNode objectNode)) {
            throw new RuntimeException("Only object nodes are allowed.");
        }

        mapping.forEach((key, value) -> {
            final var stringNode = key.k();
            final var field = key.v();
            final var found = objectNode.getValue().get(stringNode);

            value.set(found, o, field);
        });
        return o;

    }

    @Override
    public String toString() {
        return "ObjectMapper{" +
                "mapping=" + mapping +
                '}';
    }

    private interface Value {
        Node<?> getMapping(Object o);

        default Object set(Node<?> node, Object o, VarHandle vr) {
            vr.set(o, node.getValue());
            return o;
        }

        static Value of(Function<Object, Node<?>> mappingFunction, ValueSetter setFunction) {
            return new Value() {
                @Override
                public Node<?> getMapping(Object o) {
                    return mappingFunction.apply(o);
                }

                @Override
                public Object set(Node<?> node, Object o, VarHandle vr) {
                    return setFunction.set(node, o, vr);
                }
            };
        }

        interface ValueSetter {
            Object set(Node<?> node, Object o, VarHandle vr);
        }
    }
}