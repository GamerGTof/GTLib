package gt.gtlib.command.mapped;

import gt.gtlib.command.Arg;
import org.bukkit.command.CommandSender;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class InsertSenderMappedExecutor extends DefaultMappedExecutor {
    private final int senderPosition;

    protected InsertSenderMappedExecutor(MethodHandle methodHandle, Object handle, List<Arg<?>> arguments, int senderPosition) {
        super(methodHandle, handle, arguments);
        this.senderPosition = senderPosition;
    }
    @Override
    public boolean execute(CommandSender sender, Object... values) {
        final var args = new Object[values.length + 1];

        System.arraycopy(values, 0, args, 0, senderPosition);

        args[senderPosition] = sender;

        System.arraycopy(values, senderPosition, args, senderPosition + 1, values.length - senderPosition);

        return super.execute(sender, args);
    }

    @Override
    public String toString() {
        return "InsertSenderMappedExecutor{" +
                "senderPosition=" + senderPosition +
                "DefaultExecutor=" + super.toString() +
                '}';
    }
}