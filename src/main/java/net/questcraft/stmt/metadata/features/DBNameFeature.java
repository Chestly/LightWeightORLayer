package net.questcraft.stmt.metadata.features;

public class DBNameFeature implements Feature {
    private final String dbName;

    public DBNameFeature(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public String parse() {
        return this.dbName;
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }
}
