package gt.gtlib.command;

public interface TypeParser<T> {
    /**
     * Parses a 'type' from a given string.
     * Runtime exceptions thrown by this method should be treated as returning null.
     *
     * @param context Command context.
     * @return The parsed value, or null if the value could not be parsed.
     */
    T parse(CommandContext context);
}


