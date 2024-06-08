package gt.gtlib.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandContext implements Cloneable {
    private final CommandHandler handler;
    private final CommandSender sender;
    private final Command command;
    private final String[] args;
    private final List<Node> path;
    private int idx;

    public CommandContext(CommandHandler handler, Command command, CommandSender sender, String[] args, List<Node> path, int idx) {
        this.handler = handler;
        this.command = command;
        this.sender = sender;
        this.args = args;
        this.path = path;
        this.idx = idx;
    }


    public <T> TypeAdapter<T> getAdapter(Class<T> type) {
        return handler.getAdapter(type);
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public List<Node> getPath() {
        return path;
    }

    public String[] getArgs() {
        return args;
    }

    public int getIdx() {
        return idx;
    }

    public Node getRoot() {
        return path.get(0);
    }

    public Command getCommand() {
        return command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean isSenderPlayer() {
        return sender instanceof Player;
    }

    public int remainingArgs() {
        return args.length - idx;
    }

    public Node peekPath() {
        return path.get(idx);
    }

    public Node advancePath() {
        return path.get(idx++);
    }

    public String peekArg() {
        return args[idx];
    }

    public String advanceArg() {
        return args[idx++];
    }

    @Override
    public CommandContext clone() {
        CommandContext clone = null;
        try {
            clone = (CommandContext) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public String toString() {
        return "CommandContext{" +
                "handler=" + handler +
                ", sender=" + sender +
                ", command=" + command +
                ", args=" + Arrays.toString(args) +
                ", path=" + path +
                ", idx=" + idx +
                '}';
    }
}
