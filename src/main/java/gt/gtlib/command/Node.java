package gt.gtlib.command;

import java.util.*;

public interface Node {

    /**
     * @return String key for sub-nodes.
     */
    Collection<String> getKeys();

    /**
     * @return A node with the given key or null.
     */
    Node getSubNode(String key);

    Set<CommandExecutor> getHandlers();

    static Node of(Map<String, Node> subNodes, Set<CommandExecutor> handlers) {
        return new DefaultNode(handlers,subNodes);
    }



}

