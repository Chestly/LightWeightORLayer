package net.questcraft.stmt.metadata.features;

import java.util.Objects;

public class TableClauseFeature implements Feature {
    private String table;

    private AliasClauseValueFeature alias;
    private boolean isAliased = false;

    public TableClauseFeature(String table) {
        this.table = table;
    }

    public TableClauseFeature(String table, AliasClauseValueFeature alias) {
        this.table = table;
        this.alias = alias;
        this.isAliased = true;
    }

    public TableClauseFeature table(String table) {
        if (this.table == null) this.table = table;
        return this;
    }

    public String getTable() {
        return table;
    }

    @Override
    public String parse() {
        return !this.isAliased ? this.table : this.table + " " + this.alias.parse();
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableClauseFeature that = (TableClauseFeature) o;
        return Objects.equals(table, that.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table);
    }
}
