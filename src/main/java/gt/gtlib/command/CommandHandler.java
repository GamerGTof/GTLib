package gt.gtlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandHandler {

    /**
     * @param command Command called for this handler.
     * @param sender  Sender who triggered the command.
     * @param alias   Alias used when triggering the command.
     * @param args    Arguments used for the command.
     * @return True if the command was successfully handled; False otherwise.
     */
    boolean on(CommandSender sender, Command command, String alias, String[] args);

    <T> TypeAdapter<T> getAdapter(Class<T> type);
}
