package net.questcraft.utils.stringparsers;

import com.google.common.base.Splitter;
import net.questcraft.exceptions.FatalORLayerException;

import java.util.HashMap;
import java.util.Map;

public class MapParser<K, V> implements ConfigParser<Map<K, V>> {
    private ConfigParser<K> keyParser;
    private ConfigParser<V> valueParser;

    public MapParser(ConfigParser<K> keyParser, ConfigParser<V> valueParser) {
        this.keyParser = keyParser;
        this.valueParser = valueParser;
    }

    @Override
    public Map<K, V> parse(String string) throws FatalORLayerException {
        Map<String, String> unParsedMap = Splitter.on(",").withKeyValueSeparator(":").split(string);

        Map<K, V> parsedMap = new HashMap<>();
        for (String key : unParsedMap.keySet()) {
            String value = unParsedMap.get(key);

            K parsedK = keyParser.parse(key);
            V parsedV = valueParser.parse(value);

            parsedMap.put(parsedK, parsedV);
        }

        return parsedMap;
    }
}
