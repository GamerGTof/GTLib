package gt.gtlib.parser.translation;

import gt.gtlib.parser.node.*;
import gt.gtlib.parser.node.NodeVisitor;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Map;
import java.util.stream.Collectors;

public class YamlNodeTranslator implements Translator<Node> {
    private final static YamlNodeTranslator INSTANCE = new YamlNodeTranslator();
    private static final Map<Tag, NodeType> LOOKUP = Map.of(
            Tag.BOOL, NodeType.BOOLEAN,
            Tag.FLOAT, NodeType.NUMBER,
            Tag.NULL, NodeType.OBJECT,
            Tag.MAP, NodeType.OBJECT,
            Tag.INT, NodeType.NUMBER,
            Tag.STR, NodeType.STRING
    );

    private YamlNodeTranslator() {
    }

    private gt.gtlib.parser.node.Node<?> toGTNode(@NotNull Node node) {
        return switch (node.getNodeId()) {
            case mapping -> ObjectNode.of(
                    ((MappingNode) node).getValue().stream().collect(
                            Collectors.toMap(
                                    ks -> toGTNode(ks.getKeyNode()),
                                    ks -> toGTNode(ks.getValueNode())
                            )));

            case sequence ->
                    ListNode.of(((SequenceNode) node).getValue().stream().map(this::toGTNode).collect(Collectors.toList()));

            case scalar -> {
                final var scalar = (ScalarNode) node;
                final var tag = scalar.getTag();
                yield switch (LOOKUP.get(tag)) {
                    case NUMBER -> tag == Tag.FLOAT
                            ? NumberNode.of(Float.parseFloat(scalar.getValue()))
                            : NumberNode.of(Integer.parseInt(scalar.getValue())
                    );
                    case STRING -> StringNode.of(scalar.getValue());
                    case BOOLEAN -> BooleanNode.of(Boolean.valueOf(scalar.getValue()));
                    case OBJECT -> ObjectNode.of(null);
                    default -> throw new RuntimeException("Unexpected case: " + tag + ".");
                };
            }
            default -> throw new RuntimeException("Unexpected node id.");
        };
    }


    @Override
    public Node translateTo(gt.gtlib.parser.node.Node<?> root) {
        return root.accept(NODE_NODE_VISITOR);
    }

    @Override
    public gt.gtlib.parser.node.Node<?> translateFrom(Node root) {
        return toGTNode(root);
    }

    public static YamlNodeTranslator getDefault() {
        return INSTANCE;
    }

    private final NodeVisitor<Node> NODE_NODE_VISITOR = NodeVisitor.of(
            number ->
                    new ScalarNode(Tag.FLOAT,
                            String.valueOf(number.getValue().floatValue())
                            , null, null,
                            DumperOptions.ScalarStyle.PLAIN),
            bool ->
                    new ScalarNode(Tag.BOOL, bool.getValue().toString(), null, null, DumperOptions.ScalarStyle.PLAIN),
            object ->
                    new MappingNode(Tag.MAP,
                            object.getValue().entrySet().stream().map(ks -> new NodeTuple(translateTo(ks.getKey()), translateTo(ks.getValue()))).toList(),
                            DumperOptions.FlowStyle.BLOCK
                    ),
            string ->
                    new ScalarNode(Tag.STR, string.getValue(), null, null, DumperOptions.ScalarStyle.DOUBLE_QUOTED),
            list ->
                    new SequenceNode(Tag.SEQ,
                            list.getValue().stream().map(this::translateTo).toList(),
                            DumperOptions.FlowStyle.BLOCK
                    )
    );

}
