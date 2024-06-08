package gt.gtlib.command;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;

import java.util.*;

public interface TypeAdapter<T> extends TypeCompleter<T>, TypeParser<T> {
    /**
     * Default TypeAdapters
     * The map is immutable, that means it should only be used to get values.
     */
    Map<Class<?>, TypeAdapter<?>> defaultTypes = getDefaultTypes();

    static <T> TypeAdapter<T> of(TypeParser<T> parser, TypeCompleter<T> completer) {
        return new TypeAdapter<>() {

            @Override
            public T parse(CommandContext context) {
                return parser.parse(context);
            }

            @Override
            public List<String> getOptions(CommandContext context) {
                return completer.getOptions(context);
            }
        };
    }

    static <T> TypeAdapter<T> of(TypeParser<T> parser) {
        return of(parser, i -> List.<String>of());
    }

    /**
     * @return Map of immutable-default types.
     */
    private static Map<Class<?>, TypeAdapter<?>> getDefaultTypes() {
        final Map<Class<?>, TypeAdapter<?>> map = new HashMap<>(6);
        map.put(String.class, of(context -> {
            // "" bla "bla" "bla bla" " bla " "bla " " bla"
            var peeked = context.peekArg();

            if (peeked.equals("\"\"")) {
                context.advanceArg();
                return "";
            }
            if (peeked.length() > 1
                    && peeked.startsWith("\"")
                    && peeked.endsWith("\"")) {
                final var advanced = context.advanceArg();
                return advanced.substring(1, advanced.length() - 1);
            }
            final var builder = new StringJoiner(" ");
            if (!peeked.startsWith("\"")) {
                return context.advanceArg();
            }
            builder.add(peeked.substring(1));
            context.advanceArg();

            while (!(peeked = context.peekArg()).endsWith("\"")) {
                builder.add(peeked);
                context.advanceArg();
            }

            final var advanced = context.advanceArg();
            return builder.add(advanced.substring(0, advanced.length() - 1)).toString();
        }, context -> {
             var peeked = context.peekArg();

            if (peeked.equals("\"\"")) {
                return List.<String>of();
            }

            if (!peeked.startsWith("\"")){
                context.advanceArg();
                return List.of("\"");
            }
            context.advanceArg();
            if (context.remainingArgs() == 0){
                return List.of(peeked + "\"");
            }

            while (context.remainingArgs() != 0 && !context.peekArg().endsWith("\"")) {
                context.advanceArg();
            }

            if (context.remainingArgs() == 0){
                return List.of("\"");
            }
             return List.<String>of();
        }));

        map.put(Character.class, of(context -> {
            final var value = context.advanceArg();
            return value.length() > 1 ? value.charAt(0) : null;
        }));

        map.put(Integer.class, of(context -> Integer.parseInt(context.advanceArg()),
                (context -> context.advanceArg().chars().allMatch(Character::isDigit) ? List.<String>of() : null)));

        map.put(Long.class, of(context -> Long.parseLong(context.advanceArg()),
                (context -> context.advanceArg().chars().allMatch(Character::isDigit) ? List.<String>of() : null)));

        map.put(Double.class, of(context -> Double.parseDouble(context.advanceArg()),
                (context -> context.advanceArg().chars().allMatch(c -> Character.isDigit(c) || c == '.') ? List.<String>of() : null)));

        map.put(Float.class, of(context -> Float.parseFloat(context.advanceArg()),
                (context -> context.advanceArg().chars().allMatch(c -> Character.isDigit(c) || c == '.') ? List.<String>of() : null)));
        return ImmutableMap.copyOf(map);
    }
}
