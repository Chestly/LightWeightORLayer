package net.questcraft.structure.datastructure;

import java.util.Objects;

public class ObjectPrimaryIndex extends TreeNodePrimaryIndex {
    /**
     * The Value of the primary Key, Not the object holding it
     *
     */
    private final Object value;

    public ObjectPrimaryIndex(Class<?> cls, String table, String column, Object value) {
        super(cls, table, column);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ObjectPrimaryIndex that = (ObjectPrimaryIndex) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
