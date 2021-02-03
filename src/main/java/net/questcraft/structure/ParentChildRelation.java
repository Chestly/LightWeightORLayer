package net.questcraft.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ParentChildRelation<T> {
    //Member variables column and table should be of the parent, The relation will then hold its own information
    private final String column;
    private final String table;

    private final T relation;

    public ParentChildRelation(@NotNull String column, @NotNull String table, @NotNull T relation) {
        this.column = column;
        this.table = table;
        this.relation = relation;
    }

    @NotNull
    public T getRelation() {
        return this.relation;
    }

    public @NotNull String getColumn() {
        return column;
    }

    public @NotNull String getTable() {
        return table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParentChildRelation<?> that = (ParentChildRelation<?>) o;
        return Objects.equals(column, that.column) &&
                Objects.equals(table, that.table) &&
                Objects.equals(relation, that.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, table, relation);
    }
}
