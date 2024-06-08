package gt.gtlib.command;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DefaultNode implements Node {
    protected final Set<CommandExecutor> handlers;
    protected final Map<String, Node> nodes;


    public DefaultNode(Set<CommandExecutor> executors) {
        this(executors, Map.of());
    }

    public DefaultNode(Map<String, Node> nodes) {
        this(Set.of(), nodes);
    }

    public DefaultNode(Set<CommandExecutor> executors, Map<String, Node> nodes) {
        this.handlers = executors;
        this.nodes = nodes;
    }

    @Override
    public Collection<String> getKeys() {
        return nodes.keySet();
    }

    @Override
    public Node getSubNode(String key) {
        return nodes.get(key);
    }

    @Override
    public Set<CommandExecutor> getHandlers() {
        return handlers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultNode that = (DefaultNode) o;
        return Objects.equals(handlers, that.handlers) && Objects.equals(nodes, that.nodes);
    }

    @Override
    public String toString() {
        return "DefaultNode{" +
                "handlers=" + handlers +
                ", nodes=" + nodes +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(handlers, nodes);
    }
}