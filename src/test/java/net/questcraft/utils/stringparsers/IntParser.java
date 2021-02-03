package net.questcraft.utils.stringparsers;

import net.questcraft.exceptions.FatalORLayerException;

public class IntParser implements ConfigParser<Integer> {
    @Override
    public Integer parse(String string) throws FatalORLayerException {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            throw new FatalORLayerException("Unable to Parse String : '" + string + "'");
        }
    }
}
