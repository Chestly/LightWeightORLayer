package net.questcraft.utils.stringparsers;

import net.questcraft.exceptions.FatalORLayerException;

public interface ConfigParser<T> {
    T parse(String string) throws FatalORLayerException;
}
