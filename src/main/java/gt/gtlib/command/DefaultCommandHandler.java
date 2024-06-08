package gt.gtlib.command;

import gt.gtlib.exception.AdapterNotFoundException;
import gt.gtlib.utils.data.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DefaultCommandHandler implements CommandHandler, org.bukkit.command.TabCompleter {
    private final Node root;
    private final TabCompleter completer;
    private final UsageMessage formatter;
    private final HashMap<Class<?>, TypeAdapter<?>> adapters = new HashMap<>(TypeAdapter.defaultTypes);


    public DefaultCommandHandler(@NotNull Node root) {
        this(root, UsageMessage.DEFAULT);
    }

    public DefaultCommandHandler(@NotNull Node root, @Nullable UsageMessage formatter) {
        this(root, formatter, TabCompleter.DEFAULT);
    }

    public DefaultCommandHandler(@NotNull Node root, @Nullable UsageMessage formatter, @Nullable TabCompleter completer) {
        this.root = root;
        this.formatter = formatter;
        this.completer = completer;
    }

    /**
     * @return True if any value was overridden.
     */
    public <T> boolean add(Class<T> type, TypeAdapter<T> adapter) {
        return adapters.put(type, adapter) != null;
    }

    /**
     * @return True if any value was overridden.
     */
    public <T> boolean add(Class<T> type, TypeParser<T> parser, TypeCompleter<T> completer) {
        return add(type, TypeAdapter.of(parser, completer));
    }

    /**
     * @return The adapter for the type, or null if not found.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> getAdapter(Class<T> type) {
        return (TypeAdapter<T>) adapters.get(type);
    }

    @Override
    public boolean on(CommandSender sender, Command command, String alias, String[] args) {
        final var path = new ArrayList<Node>(args.length + 1);
        int i = getPath(path, args);

        final var parsedArgs = parseArgs(
                path.get(path.size() - 1)
                , args, i, command, sender, path
        );
        if (parsedArgs == null) {
            return sendUsageMessage(command, sender, args, path, i);
        }
        return parsedArgs.k().execute(sender, parsedArgs.v())
                || sendUsageMessage(command, sender, args, path, i);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (completer == null) {
            return List.of();
        }
        final var path = new ArrayList<Node>(args.length);
        final int i = getPath(path, args);
        return completer.complete(new CommandContext(this, command, sender, args, path, i));

    }

    private Pair<CommandExecutor, Object[]> parseArgs(Node node, String[] args, int start, Command command, CommandSender sender, List<Node> path) {
        final var options = node.getHandlers().stream()
                .sorted(Comparator.comparingInt(s -> -(s.getArgs().size())))
                .toList();

        for (final var option : options) {
            final List<Arg<?>> arguments = option.getArgs();
            final CommandExecutor executor = option;
            final int argumentsLen = arguments.size();
            final var objects = new Object[argumentsLen];
            final var contexts = new CommandContext[argumentsLen];
            boolean cancelled = false;
            for (int j = 0; j < argumentsLen; j++) {
                final var arg = arguments.get(j);
                final var adapter = adapters.get(arg.getType());
                if (adapter == null) {
                    throw new AdapterNotFoundException("Adapter not found for type [" + arg.getType().getName() + "].");
                }
                final var contextIdx = j == 0 ? start : contexts[j - 1].getIdx();
                final var context = new CommandContext(this, command, sender, args, path, contextIdx);
                Object obj = null;
                try {
                    obj = adapter.parse(context);
                } catch (Exception ignored) {
                }

                if (obj == null) {
                    if (arg.isNullable()) {
                        obj = arg.getDefaultValue();
                    } else {
                        cancelled = true;
                        break;
                    }
                }

                objects[j] = obj;
                contexts[j] = context;
            }

            if (!cancelled
                    && (argumentsLen == 0 && args.length == path.size() - 1)
                    || (argumentsLen > 0 && contexts[argumentsLen - 1].getIdx() >= args.length)) { // Check if is a non-arg command or if every argument was parsed.
                return Pair.of(executor, objects);
            }
        }
        return null;
    }


    private int getPath(List<Node> path, String[] args) {
        var current = root;
        int i = 0;
        while (true) {
            path.add(i, current);
            if (i >= args.length) {
                break;
            }
            final var found = current.getSubNode(args[i]);
            if (found != null) {
                current = found;
                i += 1;
            } else {
                break;
            }
        }
        return i;
    }

    private boolean sendUsageMessage(Command command, CommandSender sender, String[] args, List<Node> nodes, int index) {
        if (formatter == null) {
            return false;
        }
        sender.sendMessage(formatter.get(new CommandContext(this, command, sender, args, nodes, index)));
        return true;
    }

    @Override
    public String toString() {
        return "DefaultCommandHandler{" +
                "root=" + root +
                ", completer=" + completer +
                ", formatter=" + formatter +
                ", adapters=" + adapters +
                '}';
    }
}
