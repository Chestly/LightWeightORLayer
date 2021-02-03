package net.questcraft.utils;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.utils.stringparsers.ConfigParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigReader {
    public static String readString(String property) {
        try {
            InputStream input = new FileInputStream(System.getProperty("user.dir") + "/config.properties");
            Properties prop = new Properties();
            prop.load(input);
            Set<Object> objects = prop.keySet();
            for (Object key : objects) {
                String keyStr = (String) key;
                String value = prop.getProperty(keyStr);
                if (keyStr.equalsIgnoreCase(property)) {
                    return value;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("ConfigReader").log(Level.SEVERE, "Unable to Obtain Specified Key in config. Key: " + property);
        }
        return null;
    }

    public static <T> T getPropertiesObject(ConfigParser<T> parser, String property) throws FatalORLayerException {
        String unParsedObject = readString(property);
        return parser.parse(unParsedObject);
    }

    public static boolean getTesting() {
        try {
            InputStream input = new FileInputStream(System.getProperty("user.dir") + "/config.properties");
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("testing").equalsIgnoreCase("true");
        } catch (IOException | NullPointerException ex) {
            return false;
        }
    }
}
