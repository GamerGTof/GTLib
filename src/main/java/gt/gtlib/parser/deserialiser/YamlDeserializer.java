package gt.gtlib.parser.deserialiser;

import gt.gtlib.parser.translation.YamlNodeTranslator;
import gt.gtlib.parser.node.Node;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringReader;
import java.util.List;

public class YamlDeserializer implements Deserializer {
    private final Yaml yaml = getYaml();

    @Override
    public Node<?> deserialize(String src) {
        return YamlNodeTranslator.getDefault().translateFrom(yaml.compose(new StringReader(src)));
    }

    private Yaml getYaml() {
        final var options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        return new Yaml(options);
    }
}
