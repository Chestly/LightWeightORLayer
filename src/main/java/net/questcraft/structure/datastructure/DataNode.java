package net.questcraft.structure.datastructure;

import net.questcraft.annotations.SQLNode;
import net.questcraft.exceptions.FatalORLayerException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class DataNode<T extends DataNode<?>> {
    /**
     * The class that the specific node represents in a de-condensed form
     */
    private final Class<?> cls;

    /**
     * The Table that the node represents.
     * <p>
     * This table is specified in the {@code cls} member variable and
     * is set by the client.
     */
    private final String table;

    /**
     * The primary index of the {@code cls}. By convention this should also be the primary
     * key/index declared in the table.
     */
    private final TreeNodePrimaryIndex primaryIndex;

    /**
     * Instantiates a JavaTreeNode from the given builder
     *
     * @param builder The builder to instantiate values from
     */
    public DataNode(@NotNull Builder<?, T> builder) {
        this.cls = builder.cls;
        this.table = builder.table;
        this.primaryIndex = builder.primaryIndex;
    }


    /**
     * @return The class this tree node represents
     */
    @NotNull
    public Class<?> getCls() {
        return cls;
    }

    /**
     * @return The sql table this tree node represents
     */
    @NotNull
    public String table() {
        return table;
    }

    /**
     * @return The primary index of this node
     */
    @NotNull
    public TreeNodePrimaryIndex getPrimaryIndex() {
        return primaryIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataNode<?> that = (DataNode<?>) o;
        return Objects.equals(cls, that.cls) &&
                Objects.equals(table, that.table) &&
                //   Objects.equals(children, that.children) &&
                // Objects.equals(oneToMany, that.oneToMany) &&
                Objects.equals(primaryIndex, that.primaryIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cls,
                table,
                primaryIndex);
    }

    public static abstract class Builder<B extends Builder<?, ?>, T extends DataNode<?>> {
        private String table;
        private final Class<?> cls;

        protected TreeNodePrimaryIndex primaryIndex;

        public Builder(@NotNull String table, @NotNull Class<?> cls) {
            this.table = table;
            this.cls = cls;
        }

        protected abstract B self();

        public abstract T build() throws FatalORLayerException;

        protected boolean validate() {
            return (this.primaryIndex == null);
        }

        public B table() throws FatalORLayerException {
            if (!this.cls.isAnnotationPresent(SQLNode.class))
                throw new FatalORLayerException("Given class needs to be annotated with @SQLNode");
            this.table = this.cls.getAnnotation(SQLNode.class).value();
            return self();
        }

        public B primaryIndex(@NotNull TreeNodePrimaryIndex index) {
            this.primaryIndex = index;
            return self();
        }
    }
}
