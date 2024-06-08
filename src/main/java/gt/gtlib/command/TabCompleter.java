package gt.gtlib.command;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface TabCompleter {

    List<String> complete(CommandContext context);

    TabCompleter DEFAULT = ((context) -> {
        final var path = context.getPath();
        final var current = path.get(path.size() - 1);

        final List<String> complete = new ArrayList<>();
        final var index = context.getIdx();
        for (Iterator<List<Arg<?>>> it = current.getHandlers().stream().map(CommandExecutor::getArgs).iterator(); it.hasNext(); ) {
            List<Arg<?>> args = it.next();
            context.setIdx(index);
            if (context.getArgs().length == context.getIdx()) {
                continue;
            }
            for (Arg<?> arg : args) {
                final var adapter = context.getAdapter(arg.getType());
                final var options = adapter.getOptions(context);

                if (options == null) {
                    if (arg.isNotNullable()) {
                        break;
                    }
                    continue;
                }
                if (!options.isEmpty() && context.remainingArgs() == 0) {
                    complete.addAll(options);
                    break;
                }
            }
        }
        return complete;
    });
}
