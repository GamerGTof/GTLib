package gt.gtlib.parser.serialiser;

import gt.gtlib.parser.translation.YamlNodeTranslator;
import gt.gtlib.parser.node.Node;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

public class YamlSerializer implements Serializer {
    private final Yaml yaml = getYaml();

    private Yaml getYaml() {
        final var options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        return new Yaml(options);
    }

    @Override
    public void serialise(OutputStream outputStream, Node<?> ast) {
        final var root = YamlNodeTranslator.getDefault().translateTo(ast);
        yaml.serialize(root, new OutputStreamWriter(outputStream));
    }

    @Override
    public String serialise(@NotNull Node<?> ast) {
        final var root = YamlNodeTranslator.getDefault().translateTo(ast);
        final var out = new StringWriter();
        yaml.serialize(root, out);
        return out.toString();
    }
}
