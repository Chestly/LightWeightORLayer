package net.questcraft.structure;

import java.util.Objects;

public class OneToManyRelation<T> extends ParentChildRelation<T> {
    private final String relationalChildColumn;

    public OneToManyRelation(String column, String table, T relation, String relationalChildColumn) {
        super(column, table, relation);
        this.relationalChildColumn = relationalChildColumn;
    }

    public String getRelationalChildColumn() {
        return relationalChildColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OneToManyRelation<?> that = (OneToManyRelation<?>) o;
        return Objects.equals(relationalChildColumn, that.relationalChildColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relationalChildColumn);
    }
}
