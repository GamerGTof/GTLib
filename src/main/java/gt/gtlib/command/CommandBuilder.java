package gt.gtlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandBuilder {
    private String name, description, usageMessage;
    private List<String> aliases;
    private CommandHandler executor;
    private TabCompleter completer;

    public CommandBuilder(String name, String description, String usageMessage, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
    }

    public CommandBuilder(String name) {
        this(name, "", "", new ArrayList<>());
    }


    public CommandBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder setUsageMessage(String usageMessage) {
        this.usageMessage = usageMessage;
        return this;
    }

    public CommandBuilder setAliases(List<String> aliases) {
        this.aliases = aliases;
        return this;
    }

    public CommandBuilder setExecutor(CommandHandler executor) {
        this.executor = executor;
        return this;
    }

    public CommandBuilder setCompleter(TabCompleter completer) {
        this.completer = completer;
        return this;
    }

    public <T extends CommandHandler & TabCompleter> CommandBuilder setExecutorAndCompleter(T executorCompleter) {
        setExecutor(executorCompleter);
        setCompleter(executorCompleter);
        return this;
    }

    public Command build() {
        final var command = new DummyCommand(name, description, usageMessage, aliases == null ? new ArrayList<>() : aliases);
        command.setExecutor(executor);
        command.setTabCompleter(completer);
        return command;
    }
}
