package gt.gtlib.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class DummyCommand extends Command {
    private CommandHandler executor;
    private TabCompleter tabCompleter;

    public DummyCommand(@NotNull String name) {
        super(name);
    }

    public DummyCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (Objects.nonNull(executor)) {
            return executor.on(sender, this, commandLabel, args);
        }
        return false;
    }


    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (tabCompleter != null) {
            final var completed = tabCompleter.onTabComplete(sender, this, alias, args);
            if (completed != null) {
                return completed;
            }
        }
        return super.tabComplete(sender, alias, args);
    }


    public CommandHandler getExecutor() {
        return executor;
    }

    public void setExecutor(CommandHandler executor) {
        this.executor = executor;
    }

    public TabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public void setTabCompleter(TabCompleter tabCompleter) {
        this.tabCompleter = tabCompleter;
    }

    @Override
    public String toString() {
        return getName() + "={" + executor.toString() + ", " + tabCompleter.toString() + "}";
    }
}