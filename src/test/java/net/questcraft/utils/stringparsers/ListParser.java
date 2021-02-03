package net.questcraft.utils.stringparsers;

import com.google.common.base.Splitter;
import net.questcraft.exceptions.FatalORLayerException;

import java.util.ArrayList;
import java.util.List;

public class ListParser<T> implements ConfigParser<List<T>> {
    private ConfigParser<T> listType;

    public ListParser(ConfigParser listType) {
        this.listType = listType;
    }

    @Override
    public List<T> parse(String string) throws FatalORLayerException {
        List<String> stringList = Splitter.on(",").splitToList(string);

        List<T> parsedList = new ArrayList<>();
        for (String value : stringList) {
            parsedList.add(listType.parse(value));
        }
        return parsedList;
    }
}
