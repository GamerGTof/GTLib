package gt.gtlib.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandExecutor {

    boolean execute(CommandSender sender, Object... values);

    List<Arg<?>> getArgs();
}
