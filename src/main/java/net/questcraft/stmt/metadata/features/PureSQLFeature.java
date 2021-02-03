package net.questcraft.stmt.metadata.features;

public class PureSQLFeature implements Feature{
    private final String statement;
    private final Object[] values;

    public PureSQLFeature(String statement, Object... values) {
        this.statement = statement;
        this.values = values;
    }

    public PureSQLFeature(String statement) {
        this.statement = statement;
        this.values = new Object[0];
    }

    @Override
    public String parse() {
        return this.statement;
    }

    @Override
    public Object[] dataValues() {
        return this.values;
    }
}
