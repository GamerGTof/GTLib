package gt.gtlib.command;

import java.util.List;

public interface TypeCompleter<T> {

    /**
     * Return all possibilities to complete the parsing of the type.
     * Therefore, should return empty list when the type is completed and null if the type doesn't match.
     *
     * @param info String iterator.
     * @return Empty list if it's a complete option, null if it doesn't match the current type, list with options otherwise.
     */

    List<String> getOptions(CommandContext info);
}
