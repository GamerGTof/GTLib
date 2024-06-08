package gt.gtlib.command;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface UsageMessage {
    /**
     * @param context Command context.
     * @return Formatted string to inform how to use the corresponding command.
     */
    String get(CommandContext context);

    UsageMessage DEFAULT = ((context) -> {
        final var builder = new StringBuilder("/").append(context.getCommand().getName()).append(' ');
        final String[] args = context.getArgs();
        final List<Node> path = context.getPath();
        int index = context.getIdx();

        Node node = path.get(index);
        if (args.length > 0) {
            final var found = path.get(index).getKeys().stream()
                    .filter(s -> s.startsWith(args[index])).map(path.get(index)::getSubNode).findAny();
            if (found.isPresent()) {
                node = found.get();
            }
        }
        return builder.append(stringify(node)).toString();
    });

    private static String stringify(Node node) {
        return listed(Stream.concat(
                node.getHandlers().stream().flatMap(a -> a.getArgs().stream().map(Arg::getType).map(Class::getSimpleName)),
                node.getKeys().stream().map(s -> s + " " + stringify(node.getSubNode(s)))
        ).toList());
    }

    private static String listed(List<String> strings) {
        final var builder = new StringBuilder().append('[');
        if (!strings.isEmpty()) {
            builder.append(strings.get(0));
            for (int i = 1; i < strings.size(); i++) {
                builder.append('|').append(strings.get(i));
            }
        }
        return builder.append(']').toString();
    }
}
