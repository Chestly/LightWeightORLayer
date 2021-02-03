package net.questcraft.structure.datastructure;

import net.questcraft.stmt.metadata.features.AliasClauseValueFeature;
import net.questcraft.stmt.metadata.features.TableColumnFeature;

import java.util.Objects;

public abstract class TreeNodePrimaryIndex {
    //The Class of the value
    private final Class<?> valueClass;

    private final String table;
    private final String column;

    public TreeNodePrimaryIndex(Class<?> cls, String table, String column) {
        this.valueClass = cls;
        this.table = table;
        this.column = column;
    }

    public Class<?> getValueClass() {
        return valueClass;
    }

    public String getTable() {
        return table;
    }

    public String getColumn() {
        return column;
    }

    public TableColumnFeature toTableColumnFeature() {
        return new TableColumnFeature(this.table, this.column);
    }

    public TableColumnFeature toTableColumnFeature(AliasClauseValueFeature feature) {
        return new TableColumnFeature(this.table, this.column, feature);
    }

    public String parse() {
        return this.toTableColumnFeature().parse();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNodePrimaryIndex that = (TreeNodePrimaryIndex) o;
        return Objects.equals(valueClass, that.valueClass) &&
                Objects.equals(table, that.table) &&
                Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueClass, table, column);
    }
}
