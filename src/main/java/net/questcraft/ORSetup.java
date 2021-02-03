package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;

import java.util.HashMap;
import java.util.Map;

public class ORSetup {
    private final static Map<JDBCURL, ORLayerUtils> orLayerUtils = new HashMap<>();
    private final static Map<JDBCURL, ORLayerDBUtils> orDBUtils = new HashMap<>();



    public static ORLayerUtils configureORLayerUtils(String url, String password, String username) throws FatalORLayerException {
        final JDBCURL jdbcURL = JDBCURL.fromString(url);

        if (!orLayerUtils.containsKey(jdbcURL)) {
            orLayerUtils.put(jdbcURL, new ORLayerUtils(new DBInformation(jdbcURL, password, username)));
        }

        return orLayerUtils.get(jdbcURL);
    }

    public static ORLayerUtils configureORLayerUtils(DBInformation info) {
        if (!orLayerUtils.containsKey(info.getUrl()))
            orLayerUtils.put(info.getUrl(), new ORLayerUtils(info));
        return orLayerUtils.get(info.getUrl());
    }

    public static ORLayerDBUtils configureDBUtils(String url, String password, String username) throws FatalORLayerException {
        final JDBCURL jdbcURL = JDBCURL.fromString(url);

        if (!orDBUtils.containsKey(jdbcURL)) {
            orDBUtils.put(jdbcURL, new ORLayerDBUtils(new DBInformation(jdbcURL, password, username)));
        }

        return orDBUtils.get(jdbcURL);
    }

    public static ORLayerDBUtils configureDBUtils(DBInformation info) throws FatalORLayerException {
        final JDBCURL url = info.getUrl();

        if (!orDBUtils.containsKey(url)) {
            orDBUtils.put(url, new ORLayerDBUtils(info));
        }

        return orDBUtils.get(url);
    }
    public static class DBInformation {
        private final JDBCURL url;
        private final String password;
        private final String username;

        public DBInformation(JDBCURL url, String password, String username) {
            this.url = url;
            this.password = password;
            this.username = username;
        }

        /**
         * Not a recommended constructor, much more heavy weight
         * and can throw errors. Only to be used if the url cannot
         * be formatted prior.
         *
         * @param url The DB server URL.
         * @param password The password to the server.
         * @param username the username to the server.
         * @throws FatalORLayerException If the URL fails to format.
         */
        public DBInformation(String url, String password, String username) throws FatalORLayerException {
            this.url = JDBCURL.fromString(url);
            this.password = password;
            this.username = username;
        }

        public JDBCURL getUrl() {
            return url;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        public DBInformation toDBServer() {
            return new DBInformation(new JDBCURL.JDBCServerURL(this.url),
                    this.password,
                    this.username);
        }

    }

}