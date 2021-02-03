package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JDBCURL {
    private final String dbType;
    private final String ip;

    private final int port;
    private final boolean hasPort;

    private final String dataBase;
    private final boolean hasDB;

    private final static String REGEX_FORMATTER =
            "^jdbc:([a-zA-Z]+)" +
                    "://([\\w.]+)(?::([0-9]{1,4}))*" +
                    "/*([\\w]*)$";

    public JDBCURL(String dbType, String ip, int port, boolean hasPort, String dataBase, boolean hasDB) {
        this.dbType = dbType;
        this.ip = ip;
        this.port = port;
        this.hasPort = hasPort;
        this.dataBase = dataBase;
        this.hasDB = hasDB;
    }

    public JDBCURL(JDBCURL url, String dataBase) {
        this.dbType = url.dbType;
        this.ip = url.ip;
        this.port = url.port;
        this.hasPort = url.hasPort;
        this.dataBase = dataBase;
        this.hasDB = true;
    }

    public static JDBCURL fromString(String url) throws FatalORLayerException {

        Pattern pattern = Pattern.compile(REGEX_FORMATTER, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);

        if (!matcher.find())
            throw new FatalORLayerException("Your DB server URL is not correctly formatted! Please follow the pattern of: " +
                    "\"jdbc:DB_TYPE://ADDRESS:PORT(optional)/DATABASE(optional)\" " +
                    "(eg. \"jdbc:mariadb://localhost/ExampleDB\")");

        String dbType = matcher.group(1);
        String ip = matcher.group(2);
        String dataBase = matcher.group(4);

        try {
            int port = Integer.parseInt(matcher.group(3));
            return new JDBCURL(dbType, ip, port, true, dataBase, !dataBase.isEmpty());

        } catch (NumberFormatException e) {
            return new JDBCURL(dbType, ip, 0, false, dataBase, !dataBase.isEmpty());
        }
    }

    public String format() {
        return "jdbc:" + this.dbType +
                "://" + this.ip +
                (this.hasPort ? ":" + this.port : "") +
                (this.hasDB ? "/" + this.dataBase : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JDBCURL jdbcURL = (JDBCURL) o;
        return port == jdbcURL.port &&
                hasPort == jdbcURL.hasPort &&
                hasDB == jdbcURL.hasDB &&
                Objects.equals(dbType, jdbcURL.dbType) &&
                Objects.equals(ip, jdbcURL.ip) &&
                Objects.equals(dataBase, jdbcURL.dataBase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbType, ip, port, hasPort, dataBase, hasDB);
    }

    public static class JDBCServerURL extends JDBCURL {
        public JDBCServerURL(String dbType, String ip, int port, boolean hasPort) {
            super(dbType, ip, port, hasPort, "", false);
        }

        public JDBCServerURL(JDBCURL url) {
            super(url.dbType, url.ip, url.port, url.hasPort, "", false);
        }

        public static JDBCServerURL fromString(String url) throws FatalORLayerException {
            Pattern pattern = Pattern.compile(REGEX_FORMATTER, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(url);

            if (!matcher.find())
                throw new FatalORLayerException("Your DB server URL is not correctly formatted! Please follow the pattern of: " +
                        "\"jdbc:DB_TYPE://ADDRESS:PORT(optional)/DATABASE(optional)\" " +
                        "(eg. \"jdbc:mariadb://localhost/ExampleDB\")");

            String dbType = matcher.group(1);
            String ip = matcher.group(2);

            try {
                int port = Integer.parseInt(matcher.group(3));
                return new JDBCServerURL(dbType, ip, port, true);
            } catch (NumberFormatException e) {
                return new JDBCServerURL(dbType, ip, 0, false);
            }
        }
    }
}
