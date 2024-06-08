package gt.gtlib.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicNode extends DefaultNode {

    public DynamicNode() {
        super(new HashSet<>(), new HashMap<>());
    }

    public boolean add(CommandExecutor executor) {
        return super.handlers.add(executor);
    }

    public Node add(String name, Node subNode) {
        return super.nodes.put(name, subNode);
    }

}
