package gt.gtlib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

public final class Commands {
    private static final CommandMap commandMap;

    static {
        final var server = Bukkit.getServer();
        final var field = Reflections.getFieldOfType(CommandMap.class, server);
        commandMap = Reflections.getFieldValue(field, server, CommandMap.class);
    }

    private Commands() {
    }

    /***
     * Register a command into the server.
     * @param command The command to be registered.
     */
    public static void register(Command command) {
        commandMap.register(command.getName(), command);
    }
    public static void dispatch(CommandSender sender, String cmdArgs) {
        commandMap.dispatch(sender, cmdArgs);
    }

    public static Command getCommand(String name){
        return commandMap.getCommand(name);
    }
}
