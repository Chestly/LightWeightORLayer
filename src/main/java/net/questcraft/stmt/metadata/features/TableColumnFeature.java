package net.questcraft.stmt.metadata.features;

import java.util.Objects;

public class TableColumnFeature implements Feature {
    private final String table;
    protected final String column;
    private final boolean hasAlias;
    private AliasClauseValueFeature aliasClauseValueFeature;

    public TableColumnFeature(String table, String column) {
        this.table = table;
        this.column = column;
        this.hasAlias = false;
    }

    public TableColumnFeature(String table, String column, AliasClauseValueFeature asClauseValueFeature) {
        this.table = table;
        this.column = column;
        this.aliasClauseValueFeature = asClauseValueFeature;
        this.hasAlias = true;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public String parse() {
        return this.hasAlias ?
                table + "." + column + " " + this.aliasClauseValueFeature.parse()
                : table + "." + column;
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }

    @Override
    public String toString() {
        return this.parse();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableColumnFeature that = (TableColumnFeature) o;
        return Objects.equals(table, that.table) &&
                Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, column);
    }

    public static class PureColumnFeature extends TableColumnFeature {
        public PureColumnFeature(String column) {
            super("", column);
        }

        public PureColumnFeature(String column, AliasClauseValueFeature asClauseValueFeature) {
            super("", column, asClauseValueFeature);
        }

        @Override
        public String parse() {
            return this.column;
        }
    }
}