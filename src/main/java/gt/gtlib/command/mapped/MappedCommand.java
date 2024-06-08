package gt.gtlib.command.mapped;

import gt.gtlib.GTLib;
import gt.gtlib.command.*;
import gt.gtlib.command.linked.MethodCommandHandler;
import gt.gtlib.command.linked.NullableArg;
import gt.gtlib.command.linked.SubCommandHandler;
import gt.gtlib.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

public class MappedCommand {
    private final Object executor;
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public MappedCommand(Object executor) {
        this.executor = executor;
    }

    public List<CommandExecutor> getExecutors() {
        return Arrays.stream(executor.getClass().getDeclaredMethods())
                .filter(method ->
                        Arrays.stream(method.getAnnotations())
                                .anyMatch(annotation -> annotation instanceof MethodCommandHandler)
                ).map(this::getExecutor).toList();
    }

    public Node getNode() {
        final Map<String, Node> nodeMap = new HashMap<>();
        final Set<CommandExecutor> commandExecutors = new HashSet<>(getExecutors());
        final Map<String, String> parentMapping = new HashMap<>();
        final Map<String, List<Method>> methodMap = new HashMap<>();

        final var declaredMethods = executor.getClass().getDeclaredMethods();

        for (Method method : declaredMethods) {
            for (Annotation annotation : method.getAnnotations()) {
                if (!(annotation instanceof SubCommandHandler subCommandHandler)) {
                    continue;
                }
                parentMapping.put(method.getName(), subCommandHandler.parent());
                methodMap.computeIfAbsent(method.getName(), nm -> new ArrayList<>(1)).add(method);
            }
        }

        final Map<String, Node> nodesDefined = new HashMap<>();
        for (List<Method> value : methodMap.values()) {
            loadNode(parentMapping, nodeMap, nodesDefined, methodMap, value);
        }
        return new DefaultNode(commandExecutors, nodeMap);
    }

    private Node loadNode(Map<String, String> parentMapping, Map<String, Node> nodeMap, Map<String, Node> nodesDefined, Map<String, List<Method>> methodMap, List<Method> methods) {
        final var name = methods.get(0).getName(); // All methods have the same name, so we get the first
        final String parent = parentMapping.get(name);

        final DynamicNode node = (DynamicNode) nodesDefined.computeIfAbsent(name, nm -> {
            var n = new DynamicNode();
            methods.forEach(m -> n.add(getExecutor(m)));
            return n;
        });

        if (parent.isBlank()) {
            nodeMap.computeIfAbsent(name, nm -> node);
            return node;
        }

        final DynamicNode parentNode;
        if (nodesDefined.containsKey(parent)) {
            parentNode = (DynamicNode) nodesDefined.get(parent);
        } else {
            parentNode = (DynamicNode) loadNode(parentMapping, nodeMap, nodesDefined, methodMap, methodMap.get(parent));
        }

        parentNode.add(name, node);
        return node;
    }


    private CommandExecutor getExecutor(Method method) {
        if (!method.getReturnType().isAssignableFrom(boolean.class)) {
            throw new RuntimeException("Method return type must be a boolean value: " + method.toString());
        }

        final MethodHandle methodHandler;
        try {
            methodHandler = lookup.unreflect(method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        final Annotation[][] annotations = method.getParameterAnnotations();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final List<Arg<?>> arguments = new ArrayList<>(parameterTypes.length);

        int commandSenderPos = -1;

        for (int i = 0; i < annotations.length; i++) {
            final var notes = annotations[i];
            final var paramType = parameterTypes[i];

            if (paramType.isAssignableFrom(CommandSender.class)) {
                commandSenderPos = i;
                continue;
            }

            arguments.add(getArgFromParameter(paramType, notes));
        }

        return commandSenderPos == -1
                ? new DefaultMappedExecutor(methodHandler, executor, arguments)
                : new InsertSenderMappedExecutor(methodHandler, executor, arguments, commandSenderPos);
    }

    private Arg<?> getArgFromParameter(Class<?> paramType, Annotation[] paramAnnotations) {
        boolean nullable = false;
        for (Annotation note : paramAnnotations) {
            if (note.annotationType().isAssignableFrom(NullableArg.class)) {
                nullable = true;
            }
        }
        return nullable
                ? Arg.ofNullable(paramType)
                : Arg.of(paramType);
    }
}
