package gt.gtlib.command.mapped;

import gt.gtlib.command.Arg;
import gt.gtlib.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class DefaultMappedExecutor implements CommandExecutor {
    private final Object handle;
    private final List<Arg<?>> arguments;
    private final MethodHandle methodHandle;

    protected DefaultMappedExecutor(MethodHandle methodHandle, Object handle, List<Arg<?>> arguments) {
        this.methodHandle = methodHandle.bindTo(handle);
        this.arguments = arguments;
        this.handle = handle;
    }
    @Override
    public boolean execute(CommandSender sender, Object... values) {
        try {
            return (boolean) methodHandle.invokeWithArguments(values);
        } catch (Throwable e) {
            throw new RuntimeException("Exception on MappedExecutor for: " + handle.getClass().toString() + ".", e);
        }
    }

    @Override
    public String toString() {
        return "DefaultMappedExecutor{" +
                "handle=" + handle +
                ", arguments=" + arguments +
                ", methodHandle=" + methodHandle +
                '}';
    }

    @Override
    public List<Arg<?>> getArgs() {
        return arguments;
    }
}